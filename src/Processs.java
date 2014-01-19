import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Processs {
	public static void main(String[] args) {
		java.lang.Process process = null;
		try {
			process = Runtime.getRuntime().exec("jarsigner -verbose -keystore android_1234.p12 -storepass 1234 -signedjar signed\\mysticalcard_ykh_1anqu3.apk packed\\mysticalcard_ykh_1anqu3.apk 1 -digestalg SHA1 -sigalg MD5withRSA -storetype pkcs12");
//			process = Runtime.getRuntime().exec("net user");
			ByteArrayOutputStream resultOutStream = new ByteArrayOutputStream();
			InputStream errorInStream = new BufferedInputStream(process.getErrorStream());
			InputStream processInStream = new BufferedInputStream(process.getInputStream());
			int num = 0;
			byte[] bs = new byte[1024];
			while ((num = errorInStream.read(bs)) != -1) {
				resultOutStream.write(bs, 0, num);
			}
			while ((num = processInStream.read(bs)) != -1) {
				resultOutStream.write(bs, 0, num);
			}
			String result = new String(resultOutStream.toByteArray());
			System.out.println(result);
			File file=new File("test.txt");
			Utils.write_UTF8_FileContent(file, result);
			errorInStream.close();
			errorInStream = null;
			processInStream.close();
			processInStream = null;
			resultOutStream.close();
			resultOutStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (process != null)
				process.destroy();
			process = null;
		}
	}
}