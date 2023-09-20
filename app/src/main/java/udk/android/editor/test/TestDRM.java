package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.drm.DRMService;
import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.ProcessCallback;
import udk.android.util.Workable;
import udk.android.widget.Alerter;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestDRM extends TestBase{
	
	private boolean stop;
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
				LibConfiguration.LQ_PRERENDER = true;
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_VERTICAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = true;
				
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				
				final String downloadUrl = "http://sssss";
				DRMService drms = DRMService.getInstance();
				drms.getPkAndCreateEncParam( 
						"http://drm.unidocs.co.kr/ezpdfdrm/drmAuthority.ez?cmd=getpk", 
						"productId=aa&userId=bb", 
						new ProcessCallback<String, Exception>(){
					
							@Override
							public void onSuccess( String encedParam ){
								Alerter.shortNotice( context, encedParam );
								
								String finalUrl = downloadUrl + ( downloadUrl.indexOf( "?" ) > -1 ? "&" : "?" ) + encedParam;
								
							}
							
							@Override
							public void onFailure( Exception e ){
								
							}
						} 
					);

			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
		
//		FileUtil.copyFileSimply( "/sdcard/hang.pdf", "/sdcard/hang.copy.pdf" );
////		pdfView.openPDF( "/sdcard/hang.copy.pdf", 0 );
//		pdfView.openPDF( "/sdcard/form_filled-1.pdf", 0 );
////		pdfView.openPDF( "/sdcard/5.pdf", 0 );
		
	}//method

}//method
