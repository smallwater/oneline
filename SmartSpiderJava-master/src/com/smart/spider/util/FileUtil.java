package com.smart.spider.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {

	public static void AppendText(String filename, String content, String encoding) {

		try {

			CreateNoExistsFile(filename);

			FileOutputStream fs = new FileOutputStream(filename);

			OutputStreamWriter write = new OutputStreamWriter(fs, encoding);

			write.write(content);

			write.close();

			fs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void AppendText(String filename, String content) {

		try {

			CreateNoExistsFile(filename);

			FileWriter write = new FileWriter(filename, true);

			write.write(content);

			write.flush();

			write.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void CreateNoExistsFile(String filename) {

		try {

			File file = new File(filename);

			if (file.exists() == false) {
				file.createNewFile();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
