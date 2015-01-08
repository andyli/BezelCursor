package net.onthewings.bezelcursor;

import haxe.macro.*;
import sys.*;
import sys.io.*;

class Android {
	macro static public function setTarget(target:Int):Void {
		var sdk = Sys.getEnv("ANDROID_HOME");
		if (sdk == null) {
			for (p in [
				"/usr/local/opt/android-sdk/"
			])
			if (FileSystem.exists(p) && FileSystem.isDirectory(p)) {
				sdk = p;
				break;
			}
		}
		Compiler.addNativeLib('$sdk/platforms/android-$target/android.jar');
	}
}