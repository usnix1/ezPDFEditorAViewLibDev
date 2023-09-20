package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.pdf.ExtraOpenOptions;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.ui.PDFUIView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestUIDRM extends TestUIBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
//				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
//				LibConfiguration.UIVIEW_TITLEBAR_ENABLE = false;
				LibConfiguration.USE_FORM = true;
				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				LibConfiguration.USE_QUIZ = true;
				LibConfiguration.USE_TOOLBAR = false;
				LibConfiguration.PINCH_TO_ZOOM = false;
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method
	
	WebView webView;
	@Override            
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		 
		final Context context = this;
		
		boolean needDownload = true;
		
		if( needDownload ){
			
			RelativeLayout root = getRootContainer();
			
			webView = new WebView(context);
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
	
			webView.setWebViewClient(new WebViewClient(){
	
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					
					Log.e("", "shouldOverrideUrlLoading : " + url );
					
					if( url.startsWith("ezpdfsecure")){
						return true;
					}
					                        
					return super.shouldOverrideUrlLoading(view, url);
				}
				
			});        
			webView.setWebChromeClient(new WebChromeClient(){
	
				@Override
				public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
					Log.e("", "onConsoleMessage : " + consoleMessage.message());
						String message = consoleMessage.message();
					if( message.startsWith( "urlArr:ezpdfsecure" ) ) {
						String url = message.replace("urlArr:ezpdfsecure", "ezpdfdrm");      
						PDFUIView uiview = getPDFUIView();
						String downloadPath = "/sdcard/drm4.pdf";
						if( new File( downloadPath ).exists() ){  
							Map< String, Object > docOptions = new HashMap< String, Object >();
							uiview.open( downloadPath, null, null, 0, null, null, 0, 0, false, null, docOptions );
						}else{
							ExtraOpenOptions eoos = new ExtraOpenOptions();
							eoos.encryptedDrmFileSavePath = downloadPath;                             
							uiview.getPDFView().openPDF( url, PDFView.OPEN_PAGE_AUTO, eoos );
						}//if
						webView.setVisibility(View.INVISIBLE);
						return true;
					}
	
					return super.onConsoleMessage(consoleMessage);
				}
	
				@Override
				public void onConsoleMessage(String message, int lineNumber, String sourceID) {
	                                     				super.onConsoleMessage(message, lineNumber, sourceID);
				}
	
				@Override
				public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
					Log.e("", "onReceivedTouchIconUrl   : " + url);
					super.onReceivedTouchIconUrl(view, url, precomposed);
				}
				
			});
			RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			root.addView(webView, rlps);
			webView.loadUrl("http://redeem.unidocs.co.kr:8888/adminLogin.ez");
		} else {
	
			PDFUIView uiview = getPDFUIView();
			
			String url = "http://221.133.57.10:8888/jsp/ezPdfReader/download.jsp?encdata=FA243D365FDC742585CC019E9E1BF5DF2A22F7EE049857C62CF2DF809806A7992939D028CF8FB158FF5BFBF1D7823D10BB97A8239AE29E421D7F3EA5BBA4185C5632BDF36A4D947DBAC40CCB29F58F326B7117166E075F6FE1FD2BE6264447DB2AD8DE4C8DE4B61DAEFFCCC1A2DEC3C6E8DA3888E2C6E80F64EC2D5E45EB55EF34DA8D356CA0A62F4AD133F92A557C413FA5C6DE32505AFC84D6C32F6536EED9DDFDE37A2863958B97FC30619F3721151987F781281FC03880304E5A26B06063356F4F7BA18BC3A02D7363BCA4298EFA5DB125B94C2BE727441EB977FF30D10AD74970D641B78C5973027D74FAA8E5C47AD35FC6C88DE6C989528249B43A53A8C014A934785B2A713A7767463DF3E1CE1BA9A1A58607FC9505C7EB8BE19D5846607BDC245FC9D89185B29CAE9C6E004CA3805C32C6FD304D201C61A8BF429955C2B660635AE41D3AEB2A2C5516CDE0B4A96EDA8AE6D33CDE3FFE58CFA70C067CC98351931C7D68526001F530EF158BD3F28D46F8086ADD22068ED4857DCC8AA07C71FCE607232097CFD34999A335DB3327AC2DA9062FC9973898F16DDAFBDED9A037408F4D08E6CBFCEC86108A7B63C79466A16CCD395A352F2AE9F0DE28697CB5834613E3522950356CED9E7F294A8716E92A33597F8062861574704A72BD3CF27C3FEBDDAEC676BBF614227F27B79795D1A22E0633418D1F212D39D4C8C97A0DBECDE1FA97854394D834919C9FDE538E2E6538BC4139809A3035AD98FE4544&keys1=aaaa&keys2=bbbb&keys3=cccc";
			String downloadPath = "/sdcard/drm4.pdf";
			if( new File( downloadPath ).exists() ){  
				Map< String, Object > docOptions = new HashMap< String, Object >();
				uiview.open( downloadPath, null, null, 0, null, null, 0, 0, false, null, docOptions );
			}else{
				ExtraOpenOptions eoos = new ExtraOpenOptions();
				eoos.encryptedDrmFileSavePath = downloadPath;                             
				uiview.getPDFView().openPDF( url, PDFView.OPEN_PAGE_AUTO, eoos );
			}//if        
		}

	}//method

}//method
