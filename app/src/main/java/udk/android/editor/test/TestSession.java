package udk.android.editor.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.pdfviewlib.R;
import udk.android.util.LogUtil;

public class TestSession extends Activity{
	
	private PDFView pdfView;
	
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		LogUtil.DEBUG = true;
		
		LibConfiguration.DEBUGDRAW = false;
		
		LibConfiguration.USE_EBOOK_MODE = true;
		LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
		LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
		LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
		
		LibConfiguration.USE_ANNOTATION_CONTEXTMENU = true;
		LibConfiguration.USE_ANNOTATION_OPEN = true;
		LibConfiguration.USE_ANNOTATION_ADD_CONTEXTMENU = false;
		LibConfiguration.USE_ANNOTATION_CONTENTS_PREVIEW = false;
		LibConfiguration.USE_ANNOTATION_CONTEXTMENU_CANCEL = false;
		LibConfiguration.USE_ANNOTATION_CONTEXTMENU_FLATTEN = false;
		LibConfiguration.USE_ANNOTATION_CONTENTS_PREVIEW = false;
		LibConfiguration.USE_ANNOTATION_REPLY = false;
		LibConfiguration.USE_TEXTSELECTION_MAGNIFIER = false;
		LibConfiguration.ENABLE_DIRECT_USER_INPUT = false;
		
		Context context = this;
		
		FrameLayout fl = new FrameLayout( context );
		
		WebView wv = new WebView( context );
		WebSettings ws = wv.getSettings();
		ws.setJavaScriptEnabled( true );
		fl.addView( wv );
		
		pdfView = new PDFView( context );
		pdfView.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( R.anim.loading ) );
		
		pdfView.setVisibility( View.GONE );
		fl.addView( pdfView );
		
		wv.setWebViewClient( new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading( WebView view, String url ){
				if( url.indexOf( "contentId" ) > -1 ){
					pdfView.setVisibility( View.VISIBLE );
					pdfView.fastOpenWebPDF( url, 3 );
					return true;
				}				
				return false;
			};
		} );
		
		setContentView( fl );
		
		wv.loadUrl( "http://10.0.0.29:8080/mobileTest.jsp" );
		
	}
	
	@Override
	public void onBackPressed(){
		
		pdfView.uiAnnotV2CreateArrow();
		
//		if( pdfView != null && pdfView.isOpened() ){
//			
//			Context context = this;
//			final ProgressDialog pd = ProgressDialog.show( context, null, "종료중입니다", true, false );
//			new Thread(){
//				@Override
//				public void run(){
//					
//					pdfView.closePDF();
//					
//					runOnUiThread( new Runnable(){
//						@Override
//						public void run(){
//							pd.dismiss();
//							finish();
//						}
//					} );
//				};
//			}.start();
//			
//		}else{
//			finish();
//		}
		
	}//method

}
