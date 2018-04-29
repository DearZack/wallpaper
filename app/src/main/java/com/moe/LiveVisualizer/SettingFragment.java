package com.moe.LiveVisualizer;
import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import java.io.*;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import com.moe.LiveVisualizer.preference.SeekBarPreference;
import android.view.WindowManager;

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,Preference.OnPreferenceChangeListener
{
	private final static int WALLPAPER=0X02;
	private final static int CIRCLE=0x03;
	private final static int CROP=0x04;
	private AlertDialog delete;
	private AlertDialog circle_delete;
	private AlertDialog gif_dialog=null;
	private ListPreference color_mode,visualizer_mode;
	private DisplayMetrics display;
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		display =new DisplayMetrics();
		( (WindowManager)getActivity().getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(display);
		super.onActivityCreated(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName("moe");
		addPreferencesFromResource(R.xml.setting);
		findPreference("background").setOnPreferenceClickListener(this);
		//findPreference("artwork").setEnabled(Build.VERSION.SDK_INT>18);
		color_mode = (ListPreference) findPreference("color_mode");
		visualizer_mode = (ListPreference) findPreference("visualizer_mode");
		color_mode.setOnPreferenceChangeListener(this);
		visualizer_mode.setOnPreferenceChangeListener(this);
		onPreferenceChange(color_mode, getPreferenceManager().getSharedPreferences().getString("color_mode", "0"));
		onPreferenceChange(visualizer_mode, getPreferenceManager().getSharedPreferences().getString("visualizer_mode", "0"));

		findPreference("circle_image").setOnPreferenceClickListener(this);
		((SeekBarPreference)findPreference("borderHeight")).setMax(250);
	}

	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		switch ( p1.getKey() )
		{
			case "color_mode":
				color_mode.setSummary(color_mode.getEntries()[Integer.parseInt(p2.toString())]);
				break;
			case "visualizer_mode":
				visualizer_mode.setSummary(visualizer_mode.getEntries()[Integer.parseInt(p2.toString())]);
				break;
		} 
		return true;
	}


	@Override
	public boolean onPreferenceClick(Preference p1)
	{
		switch ( p1.getKey() )
		{
			case "background":

				/*Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
				 intent.setType("image/*");
				 startActivityForResult(intent,GET_IMAGE);*/
				final File wallpaper=new File(getActivity().getExternalCacheDir(), "wallpaper");
				if ( wallpaper.exists() )
				{
					if ( delete == null )
					{
						delete = new AlertDialog.Builder(getActivity()).setTitle("确认").setMessage("是否清除当前背景？").setPositiveButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									wallpaper.delete();
									getActivity().sendBroadcast(new Intent("wallpaper_changed"));

								}
							}).create();
					}
					delete.show();
				}
				else
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					/*intent.putExtra("crop", "true");
					 //width:height
					 Display display=getActivity().getWindowManager().getDefaultDisplay();
					 intent.putExtra("aspectX", display.getWidth());
					 intent.putExtra("aspectY", display.getHeight());
					 intent.putExtra("outputX",display.getWidth());
					 intent.putExtra("outputY",display.getHeight());
					 intent.putExtra("output", Uri.fromFile(wallpaper));
					 intent.putExtra("return-data",false);
					 intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());*/
					try
					{
						startActivityForResult(Intent.createChooser(intent, "Choose Image"), WALLPAPER);
					}
					catch (Exception e)
					{}
				}
				break;
			case "circle_image":
				final File circle=new File(getActivity().getExternalCacheDir(), "circle");
				if ( circle.exists() )
				{
					if ( circle_delete == null )
					{
						circle_delete = new AlertDialog.Builder(getActivity()).setTitle("确认").setMessage("是否清除当前图片？").setPositiveButton("取消", null).setNegativeButton("确定", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									circle.delete();
									getActivity().sendBroadcast(new Intent("circle_changed"));

								}
							}).create();
					}
					circle_delete.show();
				}
				else
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					intent.putExtra("crop", "true");
					//width:height
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("outputX", display.widthPixels / 3);
					intent.putExtra("outputY", display.widthPixels / 3);
					intent.putExtra("output", Uri.fromFile(circle));
					intent.putExtra("return-data", false);
					intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
					try
					{startActivityForResult(Intent.createChooser(intent, "Choose Image"), CIRCLE);}
					catch (Exception e)
					{}
				}
				break;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, final Intent data)
	{
		if ( resultCode == Activity.RESULT_OK )
			switch ( requestCode )
			{
				case WALLPAPER:
					if ( gif_dialog == null )
					{
						gif_dialog = new AlertDialog.Builder(getActivity()).setMessage("如何处理图片").setPositiveButton("裁剪", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									new Thread(){
										public void run()
										{
											final File tmp=new File(getActivity().getExternalCacheDir(), "tmpImage");
											final File gif=new File(getActivity().getExternalCacheDir(), "gif");
											if ( gif.exists() )gif.delete();
											FileOutputStream fos=null;
											InputStream is=null;
											try
											{
												fos = new FileOutputStream(tmp);
												is = getActivity().getContentResolver().openInputStream(data.getData());
												byte[] buffer=new byte[512];
												int len;
												while ( (len = is.read(buffer)) != -1 )
													fos.write(buffer, 0, len);
												fos.flush();
											}
											catch (IOException e)
											{}
											finally
											{
												try
												{
													if ( fos != null )fos.close();
												}
												catch (IOException e)
												{}
												try
												{
													if ( is != null )is.close();
												}
												catch (IOException e)
												{}
											}

											final File wallpaper=new File(getActivity().getExternalCacheDir(), "wallpaper");
											Intent intent = new Intent("com.android.camera.action.CROP");
											if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N )
											{
												intent.setDataAndType(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", tmp), "image/*");
												intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
											}
											else
												intent.setDataAndType(Uri.fromFile(tmp), "image/*");

											intent.putExtra("crop", "true");
											intent.putExtra("aspectX", display.widthPixels);
											intent.putExtra("aspectY", display.heightPixels);
											intent.putExtra("outputX", display.widthPixels);
											intent.putExtra("outputY", display.heightPixels);
											intent.putExtra("output", Uri.fromFile(wallpaper));
											intent.putExtra("return-data", false);
											intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
											try
											{
												startActivityForResult(intent, CROP);
											}
											catch (Exception e)
											{}

										}
									}.start();
								}
							}).setNegativeButton("GIF", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
									new Thread(){
										public void run(){
											final File tmp=new File(getActivity().getExternalCacheDir(), "wallpaper");
											final File gif=new File(getActivity().getExternalCacheDir(), "gif");
											try
											{
												if ( !gif.exists() )gif.createNewFile();
											}
											catch (IOException e)
											{}
											FileOutputStream fos=null;
											InputStream is=null;
											try
											{
												fos = new FileOutputStream(tmp);
												is = getActivity().getContentResolver().openInputStream(data.getData());
												byte[] buffer=new byte[512];
												int len;
												while ( (len = is.read(buffer)) != -1 )
													fos.write(buffer, 0, len);
												fos.flush();
											}
											catch (IOException e)
											{}
											finally
											{
												try
												{
													if ( fos != null )fos.close();
												}
												catch (IOException e)
												{}
												try
												{
													if ( is != null )is.close();
												}
												catch (IOException e)
												{}
											}
											getActivity().sendBroadcast(new Intent("wallpaper_changed"));
										}
									}.start();
								}
							}).setNeutralButton("取消", null).create();
					}
					gif_dialog.show();
					/*if(data.getData()==null)
					 getActivity().sendBroadcast(new Intent("wallpaper_changed"));
					 else{

					 }*/
					break;
				case CIRCLE:
					if ( data.getData() == null )
						getActivity().sendBroadcast(new Intent("circle_changed"));
					else
					{
						new Thread(){
							public void run()
							{
								final File tmp=new File(getActivity().getExternalCacheDir(), "tmpImage");
								FileOutputStream fos=null;
								InputStream is=null;
								try
								{
									fos = new FileOutputStream(tmp);
									is = getActivity().getContentResolver().openInputStream(data.getData());
									byte[] buffer=new byte[512];
									int len;
									while ( (len = is.read(buffer)) != -1 )
										fos.write(buffer, 0, len);
									fos.flush();
								}
								catch (IOException e)
								{}
								finally
								{
									try
									{
										if ( fos != null )fos.close();
									}
									catch (IOException e)
									{}
									try
									{
										if ( is != null )is.close();
									}
									catch (IOException e)
									{}
								}

								final File circle=new File(getActivity().getExternalCacheDir(), "circle");
								Intent intent = new Intent("com.android.camera.action.CROP");
								//可以选择图片类型，如果是*表明所有类型的图片
								if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N )
								{
									intent.setDataAndType(FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", tmp), "image/*");
									intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
								}
								else
									intent.setDataAndType(Uri.fromFile(tmp), "image/*");

								intent.putExtra("crop", "true");
								intent.putExtra("aspectX", 1);
								intent.putExtra("aspectY", 1);
								intent.putExtra("outputX", display.widthPixels / 3);
								intent.putExtra("outputY", display.widthPixels / 3);
								intent.putExtra("output", Uri.fromFile(circle));
								intent.putExtra("return-data", false);
								intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
								try
								{
									startActivityForResult(intent, CIRCLE);
								}
								catch (Exception e)
								{}

							}
						}.start();
					}
					break;
				case CROP:
					getActivity().sendBroadcast(new Intent("wallpaper_changed"));
					break;
			}
	}


}