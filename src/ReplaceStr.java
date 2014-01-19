import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReplaceStr {
	static String replaceStr;
	static String channel;//渠道号数字部分
	static String channelPre;//渠道号前缀
	static int channelMax;//渠道数量
	static String channelLast;//渠道后缀
	static boolean channelBeginWith0;//10以内渠道数字前是否含0
	static int channelStart;//起始渠道数
	
	static String unpacked="unpacked";
	static String packed="packed";
	static String signed="signed";
	static String zipaligned="zipaligned";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		channelPre= PropertyHelper.getKeyValue("channelPre");
		channelLast= PropertyHelper.getKeyValue("channelLast");
		channelMax=Integer.parseInt(PropertyHelper.getKeyValue("channelMax"));
		channelStart=Integer.parseInt(PropertyHelper.getKeyValue("channelStart"));
		channelBeginWith0=PropertyHelper.getKeyValue("channelBeginWith0").equals("true");
//		step1();//unpack
//		step2();//replace and pack
//		step2_manifest();
//		step3();//sign//		step3_1();//sign
//		step4();//zipaligned
//		step5();//clear
		
		dealArgs(args);
	}
	private static void dealArgs(String[] args) {
		// TODO Auto-generated method stub
		if(args.length==0){
			System.out.println("请提供参数");
			return;
		}else{
			if(args[0].equals("unpack")){
				step1();//unpack
			}else if(args[0].equals("repstrings_pack")){
				step2();//replace and pack
			}else if(args[0].equals("repmanifest_pack")){
				step2_manifest();//replace and pack
			}else if(args[0].equals("repmanifest_IFLYOR_FUID_pack")){
				step2_manifest_IFLYOR_FUID();
			}else if(args[0].equals("sign")){
				step3();//sign//		step3_1();//sign
			}else if(args[0].equals("zipalign")){
				step4();//zipaligned
			}else if(args[0].equals("clear")){
				step5();//clear
			}else{
				System.out.println("参数不正确");
				System.out.println("Options：");
				System.out.println("unpack  						批量解包");
				System.out.println("repstrings_pack  		批量替换strings.xml并初步打包");
				System.out.println("repmanifest_pack  	批量替换Manifest.xml并初步打包");
				System.out.println("repmanifest_IFLYOR_FUID_pack  							清理临时文件");
				System.out.println("sign  							批量签名");
				System.out.println("zipalign  						批量优化");
				System.out.println("clear  							清理临时文件");

			}
		}		
	}
	/**
	 * unpack→replace and pack
	 */
	static void stepOne(){
		step1();//unpack
		step2();//replace and pack
	}
	/**
	 * sign
	 */
	static void stepTwo(){
		step3();//sign//		step3_1();//sign
	}
	/**
	 * zipalign→clear
	 */
	static void stepThree(){
		step4();//zipaligned
		step5();//clean
	}
	private static void step5() {
		// TODO Auto-generated method stub
		System.out.println("清空临时文件→→→→→");
			Utils.deleteFolder(unpacked);
			Utils.deleteFolder(signed);
			Utils.deleteFolder(packed);
		
/*		List<String> strings= readFiles(packed);
		for(String string:strings){
			System.out.println("当前文件名："+string);
			if(string.contains(".apk")){
				new File("packed\\"+string).delete();		
			}
		}*/
		System.out.println("临时文件清理完成！");
	}

	private static void step1() {		
		Utils.createFolder(packed);
		Utils.createFolder(unpacked);
		Utils.createFolder(signed);
		Utils.createFolder(zipaligned);

		System.out.println("第一步：批量解包→→→→→");
		String dir="apk";
		List<String> strings= readFiles(dir);
		for(String string : strings){
			String temp="cmd /c apktool d \"apk\\"+string+"\" \"unpacked\\"+string.split("\\.")[0]+"\"";
			System.out.println("执行的cmd语句："+temp);
			runbat(temp);			
		}
	}

	private static void step2() {
		System.out.println("第二步：批量替换strings.xml并打包中→→→→→");
		for (int i = channelStart; i <= channelMax; i++) {
			if(i<10){
				if(channelBeginWith0){
					channel="0"+i;					
				}else{
					channel=""+i;					
				}
			}else{
				channel=String.valueOf(i);
			}
			System.out.println("正在操作渠道"+channelPre+channel+channelLast+"→→→→→");
			System.out.println("批量替换Strings.xml的fuid中→→→→→");
//			PropertyHelper.writeProperties("anqu", "anqu01");
			String dirs = "unpacked";
			List<String> stringss = readAbFiles(dirs);
			for (String temp : stringss) {
				File file = new File(temp + "\\res\\values\\strings.xml");
					//正则替换
				String regex="<string name=\"fuid\">(.*?)</string>";
				regularExp(regex, "<string name=\"fuid\">"+channelPre+channel+channelLast+"</string>", file);
			}
			System.out.println("批量打包中→→→→→");
			List<String> s_unpacks = readFiles("unpacked");
			for (String string : s_unpacks) {
				String temp = "cmd /c apktool b \"unpacked\\" + string + "\" \"packed\\" + string +channelPre+channel+channelLast+ ".apk\"";
				System.out.println("执行的cmd语句：" + temp);
				runbat(temp);
			}
		}
	}
	private static void step2_manifest(){
		System.out.println("第二步：批量替换manifest并打包中→→→→→");
		for (int i = channelStart; i <= channelMax; i++) {
			if(i<10){
				if(channelBeginWith0){
					channel="0"+i;					
				}else{
					channel=""+i;					
				}
			}else{
				channel=String.valueOf(i);
			}
			System.out.println("channel:"+channel);
			System.out.println("正在操作渠道"+channelPre+channel+channelLast+"→→→→→");
			System.out.println("批量替换manifest的UMENG_CHANNEL中→→→→→");
			String dirs = "unpacked";
			List<String> stringss = readAbFiles(dirs);
			for (String temp : stringss) {
				File file = new File(temp + "\\AndroidManifest.xml");
					//正则替换
//				String regex="<string name=\"fuid\">(.*?)</string>";
				String regex="<meta-data android:name=\"UMENG_CHANNEL\" android:value=\"(.*?)\" />";
//				regularExp("", "<string name=\"fuid\">"+channelPre+channel+channelLast+"</string>", file);
				System.out.println("替换成内容："+"<meta-data android:name=\"UMENG_CHANNEL\" android:value=\""+channelPre+channel+channelLast+"\" />");
				regularExp(regex, "<meta-data android:name=\"UMENG_CHANNEL\" android:value=\""+channelPre+channel+channelLast+"\" />", file);
//				<meta-data android:name="UMENG_CHANNEL" android:value="Original" />
			}
			System.out.println("批量打包中→→→→→");
			List<String> s_unpacks = readFiles("unpacked");
			for (String string : s_unpacks) {
				String temp = "cmd /c apktool b \"unpacked\\" + string + "\" \"packed\\" + string +channelPre+channel+channelLast+ ".apk\"";
				System.out.println("执行的cmd语句：" + temp);
				runbat(temp);
			}
		}
	}
	private static void step2_manifest_IFLYOR_FUID(){
		System.out.println("第二步：批量替换manifest并打包中→→→→→");
		for (int i = channelStart; i <= channelMax; i++) {
			if(i<10){
				if(channelBeginWith0){
					channel="0"+i;					
				}else{
					channel=""+i;					
				}
			}else{
				channel=String.valueOf(i);
			}
			System.out.println("channel:"+channel);
			System.out.println("正在操作渠道"+channelPre+channel+channelLast+"→→→→→");
			System.out.println("批量替换manifest的UMENG_CHANNEL中→→→→→");
			String dirs = "unpacked";
			List<String> stringss = readAbFiles(dirs);
			for (String temp : stringss) {
				File file = new File(temp + "\\AndroidManifest.xml");
				//正则替换
//				String regex="<string name=\"fuid\">(.*?)</string>";
				String regex="<meta-data android:name=\"IFLYOR_FUID\" android:value=\"(.*?)\" />";
//				regularExp("", "<string name=\"fuid\">"+channelPre+channel+channelLast+"</string>", file);
				System.out.println("替换成内容："+"<meta-data android:name=\"IFLYOR_FUID\" android:value=\""+channelPre+channel+channelLast+"\" />");
				regularExp(regex, "<meta-data android:name=\"IFLYOR_FUID\" android:value=\""+channelPre+channel+channelLast+"\" />", file);
//				<meta-data android:name="UMENG_CHANNEL" android:value="Original" />
			}
			System.out.println("批量打包中→→→→→");
			List<String> s_unpacks = readFiles("unpacked");
			for (String string : s_unpacks) {
				String temp = "cmd /c apktool b \"unpacked\\" + string + "\" \"packed\\" + string +channelPre+channel+channelLast+ ".apk\"";
				System.out.println("执行的cmd语句：" + temp);
				runbat(temp);
			}
		}
	}

	private static void step3() {
		System.out.println("第三步：批量签名中→→→→→");
		List<String> s_packedApks= readFiles("packed");
		for(String string : s_packedApks){
			if(!string.contains(".apk")){
				continue;
			}
//			String temp="cmd /c java -jar packed\\signapk.jar packed\\testkey.x509.pem packed\\testkey.pk8 \"packed\\"+string+"\" \"signed\\"+string+"\"";
			String temp1="jarsigner -verbose -keystore android_1234.p12 -storepass 1234 -signedjar signed\\"+string+" packed\\"+string+" 1 -digestalg SHA1 -sigalg MD5withRSA -storetype pkcs12";
			
			System.out.println("执行的签名语句："+temp1);
			runbatNowait(temp1);			
		}
		System.out.println("签名完成");
	}
	private static void step3_1() {
		System.out.println("第三步：批量签名中→→→→→");
		List<String> s_packedApks= readFiles("packed");
		for(String string : s_packedApks){
			if(!string.contains(".apk")){
				continue;
			}
//			String temp="cmd /c java -jar packed\\signapk.jar packed\\testkey.x509.pem packed\\testkey.pk8 \"packed\\"+string+"\" \"signed\\"+string+"\"";
//			String temp1="jarsigner -verbose -keystore android_1234.p12 -storepass 1234 -signedjar signed\\"+string+" packed\\"+string+" 1 -digestalg SHA1 -sigalg MD5withRSA -storetype pkcs12";
			
//			System.out.println("执行的签名语句："+temp1);
//			runbat(temp1);
			// 用一条指定的命令去构造一个进程生成器  
	        final ProcessBuilder pb = new ProcessBuilder("jarsigner.exe", "-verbose", "-keystore","android_1234.p12","-storepass","1234","-signedjar","signed\\"+string,"packed\\"+string,"1","-digestalg","SHA1","-sigalg","MD5withRSA","-storetype","pkcs12");  
	        // 让这个进程的工作区空间改为F:\dist  
	        // 这样的话,它就会去F:\dist目录下找Test.jar这个文件  
//	        pb.directory(new File("D:\\Java\\jdk1.7.0_17\\bin"));  
	        // 得到进程生成器的环境 变量,这个变量我们可以改,  
	        // 改了以后也会反应到新起的进程里面去  
	        Map<String, String> map = pb.environment();  
	        try {
	        	new Thread(){
					Process p1 = pb.start();	        		
	        	}.start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        // 然后就可以对p做自己想做的事情了  
	        // 自己这个时候就可以退出了  
		}
		System.out.println("签名完成");
		System.out.println("doOthers……");
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done!");
	}
	private static void step4() {
		System.out.println("第三步：批量优化中→→→→→");
		List<String> s_packedApks= readFiles("signed");
		for(String string : s_packedApks){
			if(!string.contains(".apk")){
				continue;
			}
//			String temp="cmd /c java -jar packed\\signapk.jar packed\\testkey.x509.pem packed\\testkey.pk8 \"packed\\"+string+"\" \"signed\\"+string+"\"";
			//zipalign 4 signed\
			String temp2="cmd /c zipalign 4 "+signed+"\\"+string+" "+zipaligned+"\\"+string;
			System.out.println("优化……");
			runbat(temp2);			
		}
		System.out.println("签名并优化完成，存放于zipaligned文件夹中。");
	}

	private static void replace(File file,String searchStr,String replaceStr) {
		// TODO Auto-generated method stub
	        if(searchStr == null){
	            return;
	        }
	        try{
/*	            FileReader reader = new FileReader(file);
	            char[] dates = new char[1024];
	            int count = 0;
	            StringBuilder sb = new StringBuilder();
	            while((count = reader.read(dates)) > 0){
	                String str = String.valueOf(dates, 0, count);
	                sb.append(str);
	            }
	            reader.close();*/
	            
	            
	            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                StringBuffer sbread = new StringBuffer();
                while (isr.ready()) {
                	sbread.append((char) isr.read());
                }
                isr.close();
	            // 从构造器中生成字符串，并替换搜索文本
	            String str = sbread.toString().replace(searchStr, replaceStr);
/*	            FileWriter writer = new FileWriter(file);
	            writer.write(str.toCharArray());
	            writer.close();*/
	            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
	            out.write(str.toCharArray());
	            out.flush();
	            out.close();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	        System.out.println("替换完成！");
	}
	/**
	 * 运行cmd命令
	 */
	public static boolean exec(String[] cmdAry) throws Exception {
		Runtime rt = Runtime.getRuntime();
		Process proc = null;
		try {
			proc = rt.exec(cmdAry); 
			/* 
			 * Runtime的exec()方法类似线程，不会在cmd命令执行完成后再继续运行下面的代码，
			 * 所以导致可能cmd命令还没执行完毕，程序就运行到了Process的destroy()方法，因
			 * 此需要一个方法去等待cmd命令执行完毕后，再运行exec()之后的方法
			 */
			return waitForProcess(proc) > 0;
		} finally {
			if (proc != null) {
				proc.destroy();
				proc = null;
			}
		}
	}
	
	/**
	 * 得到cmd命令返回的信息数据流，该流的运行周期与cmd命令的实行时间相同
	 */
	public static int waitForProcess(Process proc) throws Exception {
		// cmd命令有返回正确的信息流，和错误信息流，不过不能绝对表示cmd命令是否执行正确
		BufferedReader in = null;
		BufferedReader err = null;
		String msg = null;
		int exitValue = -1;
		try {
			in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((msg = in.readLine()) != null) {
				System.out.println(msg);
				if (1 != exitValue) {
					exitValue = 1;
				}
			}
			err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			while ((msg = err.readLine()) != null) {
				System.out.println(msg);
				if (0 != exitValue) {
					exitValue = 0;
				}
			}
			return exitValue;
			
		} finally {
			if (null != in) {
				in.close();
				in = null;
			}
			if (null != err) {
				err.close();
				err = null;
			}
		}
	}
	  public static void runbat(String bat) {

	        try {
	            Process process = Runtime.getRuntime().exec(bat);
	            process.waitFor( ); 
//	            System.out.println(ps.getInputStream());
	        } catch(IOException ioe) {
	            ioe.printStackTrace();
	        } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }  
	  public static void runbatNowait(String bat) {
		  
		  try {
			  	Runtime.getRuntime().exec(bat);
		  } catch(IOException ioe) {
			  ioe.printStackTrace();
		  }
	  }  

	  public static List<String> readFiles(String filePath) {  
	        File f = null;  
	        f = new File(filePath);  
	        File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。  
	        List<File> list = new ArrayList<File>();  
	        List<String> strings=new ArrayList<String>();
	        for (File file : files) {  
	            list.add(file);  
	        }  
	        for(File file : files) {  
	            System.out.println(file.getName());
	            strings.add(file.getName());
	            
	        }  
	        return strings;
	    }  
	  public static List<String> readAbFiles(String filePath) {  
		  File f = null;  
		  f = new File(filePath);  
		  File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。  
		  List<File> list = new ArrayList<File>();  
		  List<String> strings=new ArrayList<String>();
		  for (File file : files) {  
			  list.add(file);  
		  }  
		  for(File file : files) {  
			  strings.add(file.getAbsolutePath());
			  
		  }  
		  return strings;
	  }  
	  
	/**
	 * @param regex 正则
	 * @param to 要替换成的内容
	 * @param file 被替换的文件
	 */
	public static void regularExp(String regex,String to,File file){
//			     regex = "^\"fuid\">(.*)</string>$";      
//			     regex = "<string name=\"fuid\">(.*?)</string>";      
/*			     source = "<a href=\"http://***.***.***" onclick=\"co('**')\" class=\"lr\">***</a>";*/
				String source=Utils.read_UTF8_FileContent(file);
			    Matcher matcher = Pattern.compile(regex).matcher(source);
			    while (matcher.find()) {
			        System.out.println("匹配的group():"+matcher.group());
			        replace(file, matcher.group(),to);
			        
			    }
			    while (matcher.find()) {
			    	System.out.println("替换后的group():"+matcher.group());			    	
			    }
			/*output:
			href="http://***.***.*" onclick="co('**')" class="lr">***
			*/
	}
}














