package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.ui.PDFUIView;
import udk.android.util.Workable;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestUI extends TestUIBase{
	
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
				LibConfiguration.BOOKMARK_MANAGE_MODE = LibConfiguration.BOOKMARK_MANAGE_MODE_EXTERNAL;
//				LibConfiguration.PINCH_TO_ZOOM = false;
				
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.USE_ANNOTATION = true;
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
//		pdfView = getPDFView();
//		
//		pdfView.openPDF( "/sdcard/formatting.pdf", 0 );
		PDFUIView uiview = getPDFUIView();
//		uiview.open( path, url, is, streamLength, keys1, keys2, page, zoom, fit, params, docOptions )
		Map< String, Object > docOptions = new HashMap< String, Object >();
//		uiview.open( "/sdcard/2.A Letter to Roberto.part2.content2.pdf", null, null, 0, null, null, 0, 0, false, null, docOptions );
		uiview.open( "/sdcard/test/원숭이.pdf", null, null, 0, null, null, 0, 0, false, null, docOptions );
	}//method

}//method
