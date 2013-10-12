package com.janop.aweb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Main {
  private static String LIB_EXT = ".jar";
  private static String LAUNCHER_CLASS =
      "com.janop.aweb.launcher.ServerLauncher";

  private static ClassLoader getLibClassLoader(final ClassLoader parent)
      throws IOException {
    final Set<URL> jars = new HashSet<>();
    final File path = locateArchive();
    final JarFile jf = new JarFile(path);
    try {
      Enumeration<JarEntry> entries = jf.entries();
      while (entries.hasMoreElements()) {
        JarEntry je = entries.nextElement();
        if (!je.isDirectory() && isJar(je.getName())) {
          extract(jf, je, jars);
        }
      }
    } finally {
      jf.close();
    }
    return new URLClassLoader(jars.toArray(new URL[] {}), parent);
  }

  private static void extract(JarFile jf, JarEntry je, Set<URL> jars)
      throws IOException {
    InputStream input = null;
    OutputStream output = null;

    String name = je.getName().replace('/', '_');
    int i = name.lastIndexOf(".");
    String extension = i > -1 ? name.substring(i) : "";
    File file =
        File.createTempFile(
            name.substring(0, name.length() - extension.length()) + ".",
            extension);
    file.deleteOnExit();
    input = jf.getInputStream(je);
    try {
      output = new FileOutputStream(file);
      try {
        int readCount;
        byte[] buffer = new byte[4096];
        while ((readCount = input.read(buffer)) != -1) {
          output.write(buffer, 0, readCount);
        }
        jars.add(file.toURI().toURL());
      } finally {
        output.close();
      }
    } finally {
      input.close();
    }
  }

  private static boolean isJar(String fileName) {
    return fileName != null && fileName.toLowerCase().endsWith(LIB_EXT);
  }

  private static File locateArchive() throws FileNotFoundException {
    URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
    try {
      return new File(url.toURI());
    } catch (URISyntaxException use) {
      throw new FileNotFoundException("Cannot find the JAR's location");
    }
  }

  public static void main(String[] args) throws Exception {
    final ClassLoader libClassloader =
        getLibClassLoader(Thread.currentThread().getContextClassLoader());
    Thread.currentThread().setContextClassLoader(libClassloader);
    Class<?> launcher = Class.forName(LAUNCHER_CLASS, true, libClassloader);
    final Method launch = launcher.getMethod("launch", String[].class);
    launch.invoke(launcher.newInstance(), new Object[] {args});
  }

  private Main() {
  }
}
