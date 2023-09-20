package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.ui.PDFUIView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestUIFastWebOpen extends TestUIBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
//				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_NONE;
//				LibConfiguration.UIVIEW_TITLEBAR_ENABLE = false;
				LibConfiguration.USE_TOOLBAR = false;
				LibConfiguration.PINCH_TO_ZOOM = false;
				LibConfiguration.OPEN_METHOD_NO_CACHING = true;
				
				LibConfiguration.USE_EBOOK_MODE = true;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return null;
	}//method
	
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		PDFUIView uiview = getPDFUIView();
//		uiview.setLoadingAnimation( ( AnimationDrawable ) getResources().getDrawable( R.anim.loading ) );
		uiview.getPDFView().fastOpenWebPDF( "https://manuals.info.apple.com/MANUALS/1000/MA1595/en_US/ipad_user_guide.pdf", 0 );
		
	}//method

}//method
