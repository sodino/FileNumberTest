package lab.sodino.filenumbertest;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements Callback {
	public static final int REFRESH =  1;
	private Handler handler;
	private TextView txtProgress;
	private boolean enable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler = new Handler(this);
		
		final Button btn = (Button) findViewById(R.id.btnCreate);
		btn.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(enable){
					enable = false;
					btn.setText("CreateFile");
				}else{
					enable = true;
					btn.setText("Stop");
					startCreateThread();
				}
			}
		});
		txtProgress = (TextView) findViewById(R.id.txtProgress);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void startCreateThread(){
		new Thread(){
			public void run(){
				long startTime = System.currentTimeMillis();
				int count = 0;
				try{
					File file = Environment.getExternalStorageDirectory();
					String folderPath = file.getAbsoluteFile().getAbsolutePath() + File.separator + "localFileSystem";
					File folderTest = new File(folderPath);
					if(folderTest.exists() == false){
						folderTest.mkdirs();
					}
					while(enable){
						count ++;
						String testPath = folderPath + File.separator + Integer.toString(count);
						File testFile = new File(testPath);
						boolean result = testFile.createNewFile();
						String line = "result:" + result +" path:" + testPath;
						Log.d("ANDROID_LAB", line);
						Message msg = handler.obtainMessage();
						msg.what = REFRESH;
						if(result == false){
							line = "Clear this folder first!\n" + line;
							msg.obj = line;
							
							handler.sendMessage(msg);
							break;
						}
						msg.obj = line;
						handler.sendMessage(msg);
					}
				}catch(Exception e){
					e.printStackTrace();
					Message msg = handler.obtainMessage();
					msg.what = REFRESH;
					msg.obj ="count="+count +" timeCost=" +(System.currentTimeMillis() - startTime)+" detail:"+ e.toString();
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case REFRESH:
			txtProgress.setText(String.valueOf(msg.obj));
			break;
		}
		return false;
	}
}
