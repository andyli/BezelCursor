package net.onthewings.bezelcursor;

import java.lang.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface to the Superuser shell on Android devices with some helper functions.<p/><p/>
 * Common usage for su shell:<p/>
 * <code>if(ShellInterface.isSuAvailable()) { ShellInterface.runCommand("reboot"); }</code>
 * <p/><p/>
 * To get process output as a String:<p/>
 * <code>if(ShellInterface.isSuAvailable()) { String date = ShellInterface.getProcessOutput("date"); }</code>
 * <p/><p/>
 * To run command with standard shell (no root permissions):
 * <code>ShellInterface.setShell("sh");</code><p/>
 * <code>ShellInterface.runCommand("date");</code>
 * <p/><p/>
 * Date: Mar 24, 2010
 * Time: 4:14:07 PM
 *
 * @author serge
 */

enum OUTPUT {
  STDOUT;
  STDERR;
  BOTH;
}

class Shell {

  static var shell:String;

  // uid=0(root) gid=0(root)
  static var UID_PATTERN = Pattern.compile("^uid=(\\d+).*?");

  inline static var EXIT = "exit\n";

  static var SU_COMMANDS = [
    "su",
    "/system/xbin/su",
    "/system/bin/su"
  ];

  static var TEST_COMMANDS = [
    "id",
    "/system/xbin/id",
    "/system/bin/id"
  ];

  public static function isSuAvailable():Bool {
    if (shell == null) {
      checkSu();
    }
    return shell != null;
  }

  public static function setShell(shell:String):Void {
    Shell.shell = shell;
  }

  private static function checkSu():Bool {
    for (command in SU_COMMANDS) {
      shell = command;
      if (isRootUid()) return true;
    }
    shell = null;
    return false;
  }

  private static function isRootUid():Bool {
    var out:String = null;
    for (command in TEST_COMMANDS) {
      out = getProcessOutput(command);
      if (out != null && out.length > 0) break;
    }
    if (out == null || out.length == 0) return false;
    for (line in out.split("\n")) {
    	var matcher = UID_PATTERN.matcher(line);
        if (matcher.matches()) {
          if ("0" == matcher.group(1)) {
            return true;
          }
        }
    }
    return false;
  }

  public static function getProcessOutput(command:String):String {
    try {
      return _runCommand(command, OUTPUT.STDERR);
    } catch (ignored:IOException) {
      return null;
    }
  }

  public static function runCommand(command:String):Bool {
    try {
      _runCommand(command, OUTPUT.BOTH);
      return true;
    } catch (ignored:IOException) {
      return false;
    }
  }

  @:throws("java.io.IOException") private static function _runCommand(command:String, o:OUTPUT):String {
    var os:DataOutputStream = null;
    var process:Process = null;
    try {
      process = Runtime.getRuntime().exec(shell);
      os = new DataOutputStream(process.getOutputStream());
      var sh:InputStreamHandler = sinkProcessOutput(process, o);
      os.writeBytes(command + '\n');
      os.flush();
      os.writeBytes(EXIT);
      os.flush();
      process.waitFor();
      if (sh != null) {
        var output = sh.getOutput();
        //Log.d(TAG, command + " output: " + output);
        return output;
      } else {
        return null;
      }
    } catch (e:Exception) {
      var msg = e.getMessage();
      //Log.e(TAG, "runCommand error: " + msg);

      try {
        if (os != null) {
          os.close();
        }
        if (process != null) {
          //process.destroy();
        }
      } catch (ignored:Exception) {}

      return throw new IOException(msg);
    }

  }

  public static function sinkProcessOutput(p:Process, o:OUTPUT):InputStreamHandler {
    var output:InputStreamHandler = null;
    switch (o) {
      case STDOUT:
        output = new InputStreamHandler(p.getErrorStream(), false);
        new InputStreamHandler(p.getInputStream(), true);
      case STDERR:
        output = new InputStreamHandler(p.getInputStream(), false);
        new InputStreamHandler(p.getErrorStream(), true);
      case BOTH:
        new InputStreamHandler(p.getInputStream(), true);
        new InputStreamHandler(p.getErrorStream(), true);
    }
    return output;
  }
}

class InputStreamHandler extends Thread {
  private var stream:InputStream;
  private var sink:Bool;
  var output:StringBuffer;

  public function getOutput():String {
    return output.toString();
  }

  public function new(stream:InputStream, sink:Bool):Void {
    super();
    this.sink = sink;
    this.stream = stream;
    start();
  }

  @:overload override public function run():Void {
    try {
      if (sink) {
        while (stream.read() != -1) {}
      } else {
        output = new StringBuffer();
        var b = new BufferedReader(new InputStreamReader(stream));
        var s;
        while ((s = b.readLine()) != null) {
          output.append(s+"\n");
        }
      }
    } catch (ignored:IOException) {}
  }
}