package udk.android.editor.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.pdf.ExtraOpenOptions;
import udk.android.reader.view.pdf.PDFView;
import udk.android.pdfreaderlib.R;
import udk.android.util.AssignChecker;
import udk.android.widget.Alerter;

public class Tester extends Activity{

	private RelativeLayout rl;
	private LinearLayout ll;
	private PDFView pdfView;
	private EditText et;

	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		RelativeLayout.LayoutParams rlps = null;
		LinearLayout.LayoutParams llps = null;
		
		rl = new RelativeLayout( context );
		
		ll = new LinearLayout( context );
		ll.setOrientation( LinearLayout.VERTICAL );
		
		View v = new View( context );
		llps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		llps.weight = 1;
		ll.addView( v, llps );
		
		Button bt = new Button( context );
		bt.setText( "다운로드후 열기" );
		bt.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				
				ExtraOpenOptions eoos = new ExtraOpenOptions();
				eoos.encryptedDrmFileSavePath = new File( context.getFilesDir().getAbsolutePath() + File.separator + "download.pdf" ).getAbsolutePath();
				launch( context, eoos );

			}
		} );
		llps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		llps.weight = 0;
		ll.addView( bt, llps );
		
		bt = new Button( context );
		bt.setText( "다운로드없이 열기" );
		bt.setOnClickListener( new View.OnClickListener(){
			@Override
			public void onClick( View v ){
				launch( context, null );
			}
		} );
		llps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		llps.weight = 0;
		ll.addView( bt, llps );
		
		et = new EditText( context );
		et.setText( "http://www.kamje.or.kr/workshop/2009/0327/2.pdf" );
		llps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		llps.weight = 0;
		ll.addView( et, llps );
		
		v = new View( context );
		llps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
		llps.weight = 1;
		ll.addView( v, llps );
		
		rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
		rl.addView( ll, rlps );
		
		setContentView( rl );
	}
	
	private void launch( Context context, ExtraOpenOptions eoos ){
		
		String url = et.getText().toString();
		if( AssignChecker.isEmpty( url ) ){
			Alerter.shortNotice( context, "열람할 PDF문서의 Http URL을 입력해주세요." );
			return;
		}
		rl.removeView( ll );
		
		LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_WIDTHFIT;
		LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
		LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
		LibConfiguration.USE_EBOOK_MODE = eoos != null;
		
		pdfView = new PDFView( context );
		pdfView.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( R.anim.loading ) );
		
		RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
		rl.addView( pdfView, rlps );
		
		pdfView.openPDF( url, 1, eoos );
	}
	
	@Override
	public void onBackPressed(){
		if( pdfView != null ){
			Context context = this;
			final ProgressDialog pd = ProgressDialog.show( context, null, "문서를 닫는 중입니다", true, false );
			new Thread(){
				@Override
				public void run(){
					
					pdfView.closePDF();
					
					runOnUiThread( new Runnable(){
						@Override
						public void run(){
							pd.dismiss();
							finish();
						}
					} );
				};
			}.start();
		}else{
			super.onBackPressed();
		}
	}//method
	
}
