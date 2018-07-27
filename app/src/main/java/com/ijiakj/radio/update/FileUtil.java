package com.ijiakj.radio.update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class FileUtil
{

	public static final String PACKAGE_FILENAME = "MoMoRadio" ;
	public static String downloadDir = "MoMoRadio/uploadAPK/";// 安装目录
	// MTK平台芯片型号标识符
	private static String[] MTK_STRS = { "mt", "MT", "Helio" };
	// 高通平台芯片型号标识符
	private static String[] GT_STRS = { "qcom", "QCOM", "msm", "MSM", "mpq", "MPQ", "hi", "HI" };
	// 高通平台芯片型号标识符
	private static String[] ZX_STRS = { "sc", "SC", "SP", "sp" };
	// 手机芯片型号标识符数组集合
	private static String[][] PLATFORM_STARS = { MTK_STRS, GT_STRS, ZX_STRS };
	
	/**
	 * @return get ad pics path
	 */
	public static final String getLocalADPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "ad_pics";

	}

	/**
	 * @return the apk path for update
	 */
	public static final String getLocalApkPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "apk";
	}

	/**
	 * @return the temp path for app
	 */
	public static final String getLocalTmpPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "temp";
	}

	/**
	 * @return the download path for app
	 */
	public static final String getLocalDownloadPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "download";
	}

	public static final String getUploadTestPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "Logs" + File.separator + "com.myth.videolancher"
				+ File.separator + "2014-01-18" + File.separator + "1.log";
	}

	// 访客留影图片保存路径
	public static final String getLocalVCPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "vc_pics";
	}

	/*
	 * 留言媒体文件(视频文件，声音文件)保存路径
	 */
	public static final String getLocalVMPath()
	{
		return Environment.getExternalStorageDirectory() + File.separator + PACKAGE_FILENAME
				+ File.separator + "vm_media";
	}

	/**
	 * Comparator of files.
	 */
	public interface FileComparator
	{
		public boolean equals(File lhs, File rhs);
	}

	/**
	 * Simple file comparator which only depends on file length and modification
	 * time.
	 */
	public final static FileComparator SIMPLE_COMPARATOR = new FileComparator()
	{
		@Override
		public boolean equals(File lhs, File rhs)
		{
			return (lhs.length() == rhs.length()) && (lhs.lastModified() == rhs.lastModified());
		}
	};


	/**
	 * Copy files. If src is a directory, then all it's sub files will be copied
	 * into directory dst. If src is a file, then it will be copied to file dst.
	 * 
	 * @param src
	 *            file or directory to copy.
	 * @param dst
	 *            destination file or directory.
	 * @return true if copy complete perfectly, false otherwise (more than one
	 *         file cannot be copied).
	 */
	public static boolean copyFiles(File src, File dst)
	{
		return copyFiles(src, dst, null);
	}

	/**
	 * Copy files. If src is a directory, then all it's sub files will be copied
	 * into directory dst. If src is a file, then it will be copied to file dst.
	 * 
	 * @param src
	 *            file or directory to copy.
	 * @param dst
	 *            destination file or directory.
	 * @param filter
	 *            a file filter to determine whether or not copy corresponding
	 *            file.
	 * @return true if copy complete perfectly, false otherwise (more than one
	 *         file cannot be copied).
	 */
	public static boolean copyFiles(File src, File dst, FileFilter filter)
	{
		return copyFiles(src, dst, filter, SIMPLE_COMPARATOR);
	}

	/**
	 * Copy files. If src is a directory, then all it's sub files will be copied
	 * into directory dst. If src is a file, then it will be copied to file dst.
	 * 
	 * @param src
	 *            file or directory to copy.
	 * @param dst
	 *            destination file or directory.
	 * @param filter
	 *            a file filter to determine whether or not copy corresponding
	 *            file.
	 * @param comparator
	 *            a file comparator to determine whether src & dst are equal
	 *            files. Null to overwrite all dst files.
	 * @return true if copy complete perfectly, false otherwise (more than one
	 *         file cannot be copied).
	 */
	public static boolean copyFiles(File src, File dst, FileFilter filter, FileComparator comparator)
	{
		if (src == null || dst == null)
		{
			return false;
		}

		if (!src.exists())
		{
			return false;
		}
		if (src.isFile())
		{
			return performCopyFile(src, dst, filter, comparator);
		}

		File[] paths = src.listFiles();
		if (paths == null)
		{
			return false;
		}
		// default is true.
		boolean result = true;
		for (File sub : paths)
		{
			if (!copyFiles(sub, new File(dst, sub.getName()), filter))
			{
				result = false;
			}
		}
		return result;
	}

	@SuppressWarnings("resource")
	private static boolean performCopyFile(File srcFile, File dstFile, FileFilter filter,
			FileComparator comparator)
	{
		if (srcFile == null || dstFile == null)
		{
			return false;
		}
		if (filter != null && !filter.accept(srcFile))
		{
			return false;
		}

		FileChannel inc = null;
		FileChannel ouc = null;
		try
		{
			if (!srcFile.exists() || !srcFile.isFile())
			{
				return false;
			}

			if (dstFile.exists())
			{
				if (comparator != null && comparator.equals(srcFile, dstFile))
				{
					// equal files.
					return true;
				} else
				{
					// delete it in case of folder.
					delete(dstFile);
				}
			}

			File toParent = dstFile.getParentFile();
			if (toParent.isFile())
			{
				delete(toParent);
			}
			if (!toParent.exists() && !toParent.mkdirs())
			{
				return false;
			}

			inc = (new FileInputStream(srcFile)).getChannel();
			ouc = (new FileOutputStream(dstFile)).getChannel();

			ouc.transferFrom(inc, 0, inc.size());

		} catch (Throwable e)
		{
			e.printStackTrace();
			// exception occur, delete broken file.
			delete(dstFile);
			return false;
		} finally
		{
			try
			{
				if (inc != null)
					inc.close();
				if (ouc != null)
					ouc.close();
			} catch (Throwable e)
			{
				// empty.
			}
		}
		return true;
	}

	/**
	 * Copy asset files. If assetName is a directory, then all it's sub files
	 * will be copied into directory dst. If assetName is a file, the it will be
	 * copied to file dst.
	 * 
	 * @param context
	 *            application context.
	 * @param assetName
	 *            asset name to copy.
	 * @param dst
	 *            destination file or directory.
	 */
	public static void copyAssets(Context context, String assetName, String dst)
	{
		if (isEmpty(dst))
		{
			return;
		}
		if (assetName == null)
		{
			assetName = "";
		}
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try
		{
			files = assetManager.list(assetName);
		} catch (FileNotFoundException e)
		{
			// should be file.
			if (assetName.length() > 0)
			{
				performCopyAssetsFile(context, assetName, dst);
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		if (files == null)
		{
			return;
		}

		if (files.length == 0)
		{
			// should be file or empty dir. Try to copy it.
			if (assetName.length() > 0)
			{
				performCopyAssetsFile(context, assetName, dst);
			}
		}

		for (String file : files)
		{
			if (isEmpty(file))
				continue;

			String newAssetDir = assetName.length() == 0 ? file : assetName + File.separator + file;
			String newDestDir = dst + File.separator + file;
			copyAssets(context, newAssetDir, newDestDir);
		}
	}

	private static void performCopyAssetsFile(Context context, String assetPath, String dstPath)
	{
		if (isEmpty(assetPath) || isEmpty(dstPath))
		{
			return;
		}

		AssetManager assetManager = context.getAssets();
		File dstFile = new File(dstPath);

		InputStream in = null;
		OutputStream out = null;
		try
		{
			if (dstFile.exists())
			{
				// try to determine whether or not copy this asset file, using
				// their size.
				boolean tryStream = false;
				try
				{
					AssetFileDescriptor fd = assetManager.openFd(assetPath);
					if (dstFile.length() == fd.getLength())
					{
						// same file already exists.
						return;
					} else
					{
						if (dstFile.isDirectory())
						{
							delete(dstFile);
						}
					}
				} catch (IOException e)
				{
					// this file is compressed. cannot determine it's size.
					tryStream = true;
				}

				if (tryStream)
				{
					InputStream tmpIn = assetManager.open(assetPath);
					try
					{
						if (dstFile.length() == tmpIn.available())
						{
							return;
						} else
						{
							if (dstFile.isDirectory())
							{
								delete(dstFile);
							}
						}
					} catch (IOException e)
					{
						// do nothing.
					} finally
					{
						tmpIn.close();
					}
				}
			}

			File parent = dstFile.getParentFile();
			if (parent.isFile())
			{
				delete(parent);
			}
			if (!parent.exists() && parent.mkdirs())
			{
				return;
			}

			in = assetManager.open(assetPath);
			out = new BufferedOutputStream(new FileOutputStream(dstFile));
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}

		} catch (Throwable e)
		{
			e.printStackTrace();
			// delete broken file.
			delete(dstFile);
		} finally
		{
			try
			{
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Throwable e)
			{
				// empty.
			}
		}
	}

	/**
	 * Delete corresponding path, file or directory.
	 * 
	 * @param file
	 *            path to delete.
	 */
	public static void delete(File file)
	{
		delete(file, false);
	}

	/**
	 * Delete corresponding path, file or directory.
	 * 
	 * @param file
	 *            path to delete.
	 * @param ignoreDir
	 *            whether ignore directory. If true, all files will be deleted
	 *            while directories is reserved.
	 */
	public static void delete(File file, boolean ignoreDir)
	{
		if (file == null || !file.exists())
		{
			return;
		}
		if (file.isFile())
		{
			file.delete();
			return;
		}

		File[] fileList = file.listFiles();
		if (fileList == null)
		{
			return;
		}

		for (File f : fileList)
		{
			delete(f, ignoreDir);
		}
		// delete the folder if need.
		if (!ignoreDir)
			file.delete();
	}

	private static boolean isEmpty(String str)
	{
		return str == null || str.length() == 0;
	}

	/**
	 * create file.if dir not exist, dir will be created.
	 * 
	 * @param file
	 */
	public static void createIfNotExist(File file)
	{
		if (file.exists())
		{
			return;
		}

		try
		{

			if (file.getParentFile() != null && !file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}

			file.createNewFile();
		} catch (IOException e)
		{
			Log.e("FileUtil", "Create file " + file.getPath() + " error : ", e);
		}
	}

	/**
	 * get file Name from path.If path is empty,return the current time as file
	 * name
	 * 
	 * @param path
	 * @return filename;
	 */
	public static String generateFileName(String path)
	{
		String fileName = null;
		if (!TextUtils.isEmpty(path))
		{
			String tempName = path.trim();
			String[] temp = tempName.split("/");
			if (temp != null && temp.length > 1)
			{
				fileName = temp[temp.length - 1];
			}
		}

		if (fileName == null)
		{
			fileName = String.valueOf(System.currentTimeMillis());
		}
		return fileName;
	}

	/**
	 * 删除SDCard的录音文件
	 * 
	 * @param fileName
	 *            如果为NULL删除所有的.amr 和 .3gp的文件 。 不为NULL删除目录下的文件
	 */
	public static void deleteSDCardFile(String fileName)
	{
		File file = new File(getLocalVMPath());
		if (TextUtils.isEmpty(fileName)) // 删除全部
		{
			if (file.exists() && file.isDirectory())
			{
				File[] files = file.listFiles();
				if (files.length > 0)
				{
					for (File file2 : files)
					{
						file2.delete();
					}
				}
			}
			return;
		} else
		{
			file = new File(getLocalVMPath() + File.separator + fileName);
			if (file.exists() && file.isFile())
			{
				file.delete();
			}
		}

	}
	
	/*
	 * 删除当前文件夹下的所有文件，包括文件夹且不删除当前文件夹
	 */
	public static void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
//	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}
	
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
//				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
	
	public static File updateDir = null;
	public static File updateFile = null;

	/***
	 * 创建文件  返回文件的绝对路径
	 */
	public static String  createFile(String name) {
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + downloadDir);
			updateFile = new File(updateDir + "/" + name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
					
					return updateFile.getAbsolutePath() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				return updateFile.getAbsolutePath() ;
			}

		}
		return null ;
	}
	
	/**判断文件是否存在*/
	public static boolean fileIsExists(String name) {
		File updateFile = new File(getAPKPath(name));
		try {
			if (!updateFile.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**返回apk完整路径（包括名称）*/
	public static String getAPKPath(String name){
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + downloadDir);
			return updateDir + "/" + name + ".apk";
		}
		return "";
	}
	
	/**
	 * 判断将要安装的apk是否可以安装 1.先校验apk的包名对不对， 2.再校验版本号是不是大于等于服务器和当前已安装的版本号，
	 * 3.最后校验完整性（写在安装代码里了）
	 */
	public static boolean ifCanInstallApk(Context context, String name,
			String serveVersionCode) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo filePackageInfo = pm.getPackageArchiveInfo(
					getAPKPath(name), PackageManager.GET_ACTIVITIES);

			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			if (fileIsExists(name)
					&& filePackageInfo.packageName
							.equals(packageInfo.packageName)
					&& filePackageInfo.versionCode>packageInfo.versionCode
					&& filePackageInfo.versionCode>=Integer.parseInt(serveVersionCode)) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/** 删除文件夹 */
	public static void deleteFile(File oldPath, String oldPoint) {
		if (oldPath.exists()) {
			if (oldPath.isDirectory()) {
				File[] files = oldPath.listFiles();
				for (File file : files) {
					if (!TextUtils.isEmpty(oldPoint)&&file.toString().contains(oldPoint)) {
						continue;
					}
					deleteFile(file ,null);
					file.delete();
				}
			} else {
				oldPath.delete();
			}
		}
	}
	
	/** 判断当前语言环境(由于繁体在使用getLanguage()时也会返回zh，所以使用getCountry()方法) */
	public static String getLanguage(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String country = locale.getCountry();
		if (!TextUtils.isEmpty(country)) {
			return country;
		}
		return "zh";
	}
	
	


	/**
		 * 获取手机平台
		 * 
		 * @return
		 */
		public static int getPlatformType() {
			String platformStr = getPlatformStr();
			for (int i = 0; i < PLATFORM_STARS.length; i++) {
				String[] platformStrsItem = PLATFORM_STARS[i];
				for (int j = 0; j < platformStrsItem.length; j++) {
					if (platformStr != null
							&& platformStr.startsWith(platformStrsItem[j])) {
						return i + 1;
					}
				}
			}
			return 0;
		}

		private static String getPlatformStr() {
			try {
				Field[] fields = Build.class.getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					String name = field.getName();
					if (name.equals("HARDWARE") || name.equals("hardware")) {
						String value = field.get(null).toString();
						return value;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
}
