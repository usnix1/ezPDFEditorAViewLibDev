package udk.android.editor.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFastWebOpen extends TestBase{
	
	private boolean stop;
	private PDFView pdfView;
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_EBOOK_MODE = false;
				LibConfiguration.CONTINUOUS_SCROLL_TYPE = LibConfiguration.CONTINUOUS_SCROLL_TYPE_HORIZONTAL;
				LibConfiguration.CONTINUOUS_SCROLL_SEAMLESS = false;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				
//				LibConfiguration.OPEN_METHOD_NO_CACHING = false;
				
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				pdfView.nextPage();
			}//method
		};
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		pdfView = getPDFView();
//		pdfView.fastOpenWebPDF( "http://webviewer.unidocs.co.kr/ezpdfwebviewer/sviewer.jsp?contentId=sample2.pdf&reqType=docData", 0 );
//		pdfView.fastOpenWebPDF( "http://webviewer.unidocs.co.kr/ezpdfwebviewer/sviewer.jsp?reqType=docData&contentId=0", 0 );
		pdfView.fastOpenWebPDF( "https://manuals.info.apple.com/MANUALS/1000/MA1595/en_US/ipad_user_guide.pdf", 0 );
		
	}//method
	
	@Override
	public void onBackPressed(){
		
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
		
	}//method

}//method
