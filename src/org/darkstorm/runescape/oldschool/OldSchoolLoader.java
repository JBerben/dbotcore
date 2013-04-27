package org.darkstorm.runescape.oldschool;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

import org.apache.bcel.generic.ClassGen;
import org.darkstorm.bcel.*;
import org.darkstorm.bcel.deobbers.*;
import org.darkstorm.bcel.deobbers.EuclideanNumberDeobber.EuclideanNumberPair;
import org.darkstorm.bcel.hooks.*;
import org.darkstorm.runescape.*;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.oldschool.transformers.*;
import org.darkstorm.runescape.util.*;
import org.jdom.Element;
import org.jdom.output.*;

public class OldSchoolLoader implements Loader, AppletStub, AppletContext {
	public static final String HOOKS_PACKAGE = OldSchoolLoader.class
			.getPackage().getName() + ".hooks.I";

	private final OldSchoolBot bot;
	private final Logger logger;
	private final int world;
	private final Map<String, String> parameters;
	private final Map<String, InputStream> streams = new HashMap<String, InputStream>();

	private CustomClassLoader classLoader;
	private Applet applet;

	public OldSchoolLoader(OldSchoolBot bot, int world) {
		this.bot = bot;
		logger = bot.getLogger();
		this.world = world;
		parameters = new HashMap<String, String>();
	}

	public OldSchoolLoader(Logger logger, int world) {
		bot = null;
		this.logger = logger;
		this.world = world;
		parameters = new HashMap<String, String>();
	}

	@Override
	public void load(Cache cache, Status status) throws IOException {
		status.setMessage("Loading");
		String jarName = loadPage(cache);
		byte[] data;
		if(!cache.isCached("launcher")) {
			status.setMessage("Loading game");
			data = download(new URL("http://oldschool" + world
					+ ".runescape.com/" + jarName), status);
			cache.saveCache("launcher", data);
		} else
			data = cache.loadCache("launcher");
		String xml;
		if(!cache.isCached("hooks") || "".equals("")) {
			status.setMessage("Identifying");
			Updater updater = new Updater(logger, HOOKS_PACKAGE, data);
			// updater.registerTransformer(new TestTransformer(updater));
			updater.registerTransformer(new ClientTransformer(updater));
			updater.registerTransformer(new NodeTransformer(updater));
			updater.registerTransformer(new NodeSubTransformer(updater));
			updater.registerTransformer(new AnimableTransformer(updater));
			updater.registerTransformer(new CharacterTransformer(updater));
			updater.registerTransformer(new PlayerTransformer(updater));
			updater.registerTransformer(new ModelTransformer(updater));
			updater.registerTransformer(new NPCDefTransformer(updater));
			updater.registerTransformer(new NPCTransformer(updater));
			updater.registerTransformer(new MouseTransformer(updater));
			updater.registerTransformer(new KeyboardTransformer(updater));
			updater.registerTransformer(new CanvasTransformer(updater));
			updater.registerTransformer(new InterfaceTransformer(updater));
			/*updater.registerTransformer(new Transformer(updater) {
				{
					// HUEHUEHUEHUEHUEHUE
					// ...nothing going on here...
					HookLoader loader = new WBotXMLHookLoader(updater
							.getClasses(), HOOKS_PACKAGE, Mouse.class
							.getPackage().getName() + ".");
					loader.load();
					hooks.addAll(Arrays.asList(loader.getHooks()));
				}

				@Override
				public void updateHook(ClassGen classGen) {
					hooks.add(new FieldHook(classGen.getClassName(), updater
							.getHooksPackage() + "Client", "bot",
							org.apache.bcel.generic.Type.getType(
									OldSchoolBot.class).getSignature(), true,
							OldSchoolBot.class.getName(), "getBot", "setBot"));
				}

				@Override
				public boolean isLocatedIn(ClassGen classGen) {
					return classGen.getClassName().equals("client");
				}
			});*/
			updater.injectTransforms(status);
			updater.printHooks();
			updater.printFailed();
			xml = updater.generateXML();
			System.out.println(xml);
			cache.saveCache("hooks", xml.getBytes());
		} else
			xml = new String(cache.loadCache("hooks"));
		HookLoader hookLoader = new XMLHookLoader(xml);
		hookLoader.load();
		Hook[] hooks = hookLoader.getHooks();

		List<ClassGen> newClasses = new ArrayList<ClassGen>();

		status.setMessage("Generating API");
		ClassGenerator classGenerator = new ClassGenerator(hooks);
		classGenerator.generateClasses(status);
		classGenerator.dumpJar(new File("api.jar"));
		newClasses.addAll(Arrays.asList(classGenerator.getClasses()));

		Injector injector = new Injector(data, hooks);
		Map<String, EuclideanNumberPair> pairs;
		if(!cache.isCached("euclid_pairs")) {
			EuclideanNumberDeobber numberDeobber = new EuclideanNumberDeobber(
					injector);
			injector.registerDeobber(numberDeobber);
			status.setMessage("Deobbing");
			injector.deobfuscate(status);
			pairs = numberDeobber.getFinalPairs();
			StringBuffer lineData = new StringBuffer();
			for(String key : pairs.keySet()) {
				EuclideanNumberPair pair = pairs.get(key);
				lineData.append(key).append(":");
				lineData.append(pair.product()).append(":");
				lineData.append(pair.quotient()).append(":");
				lineData.append(pair.gcd()).append(":");
				lineData.append(pair.bits()).append(":");
				lineData.append(pair.isUnsafe()).append("\n");
			}
			cache.saveCache("euclid_pairs", lineData.toString().getBytes());
		} else {
			pairs = new HashMap<String, EuclideanNumberPair>();
			String[] euclideanPairsUnparsed = new String(
					cache.loadCache("euclid_pairs")).split("\n");
			for(String line : euclideanPairsUnparsed) {
				if(line.trim().isEmpty())
					continue;
				try {
					String[] lineData = line.split(":");
					if(lineData.length != 6)
						continue;
					String key = lineData[0];
					BigInteger product = new BigInteger(lineData[1]);
					BigInteger quotient = new BigInteger(lineData[2]);
					BigInteger gcd = new BigInteger(lineData[3]);
					int bits = Integer.parseInt(lineData[4]);
					boolean unsafe = Boolean.parseBoolean(lineData[5]);
					EuclideanNumberPair pair = new EuclideanNumberPair(product,
							quotient, gcd, bits, unsafe);
					pairs.put(key, pair);
				} catch(Exception exception) {}
			}
		}
		MMIRepository.init(pairs);
		status.setMessage("Injecting");
		injector.injectHooks(status);
		// status.setMessage("Deobbing");
		// injector.registerDeobber(new ZKMFlowDeobber(injector));
		// injector.registerDeobber(new ExceptionTableDeobber(injector));
		// injector.registerDeobber(new DeadCodeDeobber(injector));
		// injector.registerDeobber(new DiffDeobber(injector));
		injector.registerDeobber(new TreeBuilderDeobber(injector));
		injector.deobfuscate(status);
		injector.dumpJar(new File("dumped.jar"));
		newClasses.addAll(injector.getClasses());

		String message = "Outputting final";
		status.setMessage(message);
		classLoader = new CustomClassLoader(new ProtectionDomain(
				new CodeSource(new URL("http://runescape.com/"),
						(CodeSigner[]) null), getPermissions()));
		int progress = 0;
		for(ClassGen classGen : newClasses) {
			classLoader.addClass(classGen.getClassName(), classGen
					.getJavaClass().getBytes());
			status.setProgress((int) ((++progress / (double) newClasses.size()) * 100));
			status.setMessage(message + " - " + progress + "/"
					+ newClasses.size() + " (" + status.getProgress() + "%)");
		}
		System.out.println(new File("../DarkBot-OldSchoolImpl/bin")
				.getAbsolutePath());
		classLoader.addURL(new File("../DarkBot-OldSchoolImpl/bin").toURI()
				.toURL());
	}

	private Permissions getPermissions() {
		Permissions ps = new Permissions();
		ps.add(new AWTPermission("accessEventQueue"));
		ps.add(new PropertyPermission("user.home", "read"));
		ps.add(new PropertyPermission("java.vendor", "read"));
		ps.add(new PropertyPermission("java.version", "read"));
		ps.add(new PropertyPermission("os.name", "read"));
		ps.add(new PropertyPermission("os.arch", "read"));
		ps.add(new PropertyPermission("os.version", "read"));
		ps.add(new SocketPermission("*", "connect,resolve"));
		String uDir = System.getProperty("user.home");
		if(uDir != null) {
			uDir += "/";
		} else {
			uDir = "~/";
		}
		String[] dirs = { "c:/rscache/", "/rscache/", "c:/windows/",
				"c:/winnt/", "c:/", uDir, "/tmp/", "." };
		String[] rsDirs = { ".jagex_cache_32", ".file_store_32" };
		for(String dir : dirs) {
			File f = new File(dir);
			ps.add(new FilePermission(dir, "read"));
			if(!f.exists())
				continue;
			dir = f.getPath();
			for(String rsDir : rsDirs) {
				ps.add(new FilePermission(dir + File.separator + rsDir
						+ File.separator + "-", "read"));
				ps.add(new FilePermission(dir + File.separator + rsDir
						+ File.separator + "-", "write"));
			}
		}
		ps.setReadOnly();
		return ps;
	}

	private byte[] download(URL url, Status status) throws IOException {
		String message = status.getMessage();
		status.setProgress(0);
		URLConnection uc = url.openConnection();
		uc.addRequestProperty("Host", "oldschool" + world + ".runescape.com");
		uc.addRequestProperty("Connection", "keep-alive");
		uc.addRequestProperty("Cache-Control", "max-age=0");
		uc.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.19 (KHTML, like Gecko) Ubuntu/11.10 Chromium/18.0.1025.151 Chrome/18.0.1025.151 Safari/535.19");
		uc.addRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		uc.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
		uc.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		uc.addRequestProperty("Accept-Charset",
				"ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		int len = uc.getContentLength();
		InputStream is = new BufferedInputStream(uc.getInputStream());
		try {
			byte[] data = new byte[len];
			int offset = 0;
			while(offset < len) {
				int read = is.read(data, offset, data.length - offset);
				if(read < 0)
					break;
				offset += read;
				status.setProgress((int) ((offset / (double) len) * 100));
				status.setMessage(message + " - " + status.getProgress() + "%");
			}
			if(offset < len)
				throw new IOException(String.format(
						"Read %d bytes; expected %d", offset, len));
			status.setProgress(100);
			return data;
		} finally {
			is.close();
		}
	}

	private String loadPage(Cache cache) throws IOException {
		String appletArchive = null;
		if(!cache.isCached("parameters")) {
			URL worldURL = new URL("http://oldschool" + world
					+ ".runescape.com/j1");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					worldURL.openStream()));
			String line;
			StringBuffer cacheText = new StringBuffer();
			while((line = reader.readLine()) != null) {
				if(line.contains("<param name=")) {
					String key = line.split("<param name=\"")[1].split("\" ")[0];
					String value = line.split("value=\"")[1].split("\">'")[0];
					if(value.isEmpty())
						value = " ";
					parameters.put(key, value);
					cacheText.append("key=\"").append(key)
							.append("\",value=\"").append(value).append("\"\n");
					logger.finer("Found applet parameter, key: " + key
							+ ", value: " + value);
				} else if(line.contains("archive=gamepack_")) {
					appletArchive = line.split("archive=")[1].split(" ")[0];
					cacheText.append("archive=\"").append(appletArchive)
							.append("\"\n");
					logger.finer("Found applet archive: " + appletArchive);
				}
			}
			reader.close();
			cache.saveCache("parameters", cacheText.toString().getBytes());
		} else {
			String[] lines = new String(cache.loadCache("parameters"))
					.split("\n");
			for(String line : lines) {
				if(line.contains("key=")) {
					String key = line.split("key=\"")[1].split("\"")[0];
					String value = line.split("value=\"")[1].split("\"")[0];
					parameters.put(key, value);
				} else if(line.contains("archive="))
					appletArchive = line.split("archive=\"")[1].split("\"")[0];
			}
		}
		return appletArchive;
	}

	@Override
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Applet createApplet(Cache cache, Status status) {
		status.setMessage("Starting game");
		status.setProgress(0);
		if(applet == null) {
			try {
				applet = (Applet) classLoader.loadClass("client").newInstance();
				Field botField = applet.getClass().getField("bot");
				botField.set(null, bot);

				applet.setPreferredSize(new Dimension(765, 503));
				applet.setSize(applet.getPreferredSize());
				applet.setBackground(Color.BLACK);
				applet.setStub(this);

				status.setProgress(50);
				applet.init();
				applet.start();
			} catch(RuntimeException exception) {
				throw exception;
			} catch(Exception exception) {
				throw new RuntimeException(exception);
			}
		}
		status.setProgress(100);
		return applet;
	}

	@Override
	public GameContext createContext() {
		if("".equals(""))
			return null;
		try {
			Class<?> c = classLoader
					.loadClass("org.darkstorm.runescape.oldschool.impl.GameContextImpl");
			Constructor<?> constructor = c.getConstructor(OldSchoolBot.class);
			return (GameContext) constructor.newInstance(bot);
		} catch(RuntimeException exception) {
			throw exception;
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public URL getDocumentBase() {
		try {
			return new URL("http://oldschool" + world + ".runescape.com");
		} catch(MalformedURLException exception) {
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL("http://oldschool" + world + ".runescape.com");
		} catch(MalformedURLException exception) {
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public AppletContext getAppletContext() {
		return this;
	}

	@Override
	public void appletResize(int width, int height) {
	}

	@Override
	public AudioClip getAudioClip(URL url) {
		return new AudioClipSub(url);
	}

	@Override
	public Image getImage(URL url) {
		return Toolkit.getDefaultToolkit().createImage(url);
	}

	@Override
	public Applet getApplet(String name) {
		return null;
	}

	@Override
	public Enumeration<Applet> getApplets() {
		return new Enumeration<Applet>() {
			int currentElement = 0;

			@Override
			public Applet nextElement() {
				currentElement++;
				if(currentElement != 1)
					throw new NoSuchElementException();
				return applet;
			}

			@Override
			public boolean hasMoreElements() {
				return false;
			}
		};
	}

	@Override
	public void showDocument(URL url) {
		logger.finer("Attempting to display: " + url);
	}

	@Override
	public void showDocument(URL url, String target) {
		showDocument(url);
	}

	@Override
	public void showStatus(String status) {
		logger.finer("Status: " + status);
	}

	@Override
	public void setStream(String key, InputStream stream) throws IOException {
		streams.put(key, stream);
	}

	@Override
	public InputStream getStream(String key) {
		return streams.get(key);
	}

	@Override
	public Iterator<String> getStreamKeys() {
		return streams.keySet().iterator();
	}

	public void close() {
		applet = null;
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		HookLoader loader = new WBotXMLHookLoader(null,
				"org.darkstorm.runescape.oldschool.hooks.",
				"org.darkstorm.runescape.oldschool.overrides.");
		loader.load();
		Hook[] hooks = loader.getHooks();
		Element root = new Element("hooks");
		Map<String, Element> interfaceElements = new HashMap<String, Element>();
		for(Hook hook : hooks) {
			if(hook instanceof InterfaceHook) {
				Element element = hook.toXML();
				interfaceElements.put(hook.getInterfaceName(), element);
				root.addContent(element);
			}
		}
		for(Hook hook : hooks)
			if(!(hook instanceof InterfaceHook))
				interfaceElements.get(hook.getInterfaceName()).addContent(
						hook.toXML());
		StringWriter writer = new StringWriter();
		new XMLOutputter(Format.getPrettyFormat()).output(root, writer);
		System.out.println(writer.toString());
		if("".equals(""))
			return;
		Class.forName(DarkBot.class.getName());
		Cache cache = new DirectoryCache(new File("cache"));
		Status status = new Status() {

			@Override
			public void setProgressShown(boolean progressShown) {
			}

			@Override
			public void setProgress(int progress) {
			}

			@Override
			public void setMessage(String message) {
			}

			@Override
			public boolean isProgressShown() {
				return false;
			}

			@Override
			public int getProgress() {
				return 0;
			}

			@Override
			public String getMessage() {
				return "";
			}
		};
		new OldSchoolLoader(Logger.getLogger("Bot"), 78).load(cache, status);
	}

	private static class AudioClipSub implements AudioClip {
		public static final short STATE_STOPPED = 0, STATE_PLAYING = 1,
				STATE_LOOPING = 2;

		private final URL sourceURL;

		private short audioClipState;

		public AudioClipSub(URL sourceURL) {
			this.sourceURL = sourceURL;
			audioClipState = STATE_STOPPED;
		}

		public short getAudioClipState() {
			return audioClipState;
		}

		public URL getURL() {
			return sourceURL;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			if(obj == this)
				return true;
			if(!(obj instanceof AudioClip))
				return false;
			AudioClipSub ac = (AudioClipSub) obj;
			return ac.getAudioClipState() == audioClipState
					&& ac.getURL().equals(sourceURL);
		}

		@Override
		public void play() {
			audioClipState = STATE_PLAYING;
		}

		@Override
		public void loop() {
			audioClipState = STATE_LOOPING;
		}

		@Override
		public void stop() {
			audioClipState = STATE_STOPPED;
		}
	}
}