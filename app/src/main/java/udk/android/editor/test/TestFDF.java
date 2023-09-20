package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.GlobalConfigurationService;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.PDFViewAdapter;
import udk.android.editor.view.pdf.PDFViewEvent;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFDF extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USER_LOG_LEVEL = LibConfiguration.USER_LOG_LEVEL_FATAL;
				LibConfiguration.USE_TOOLBAR = true;
				LibConfiguration.USE_ANNOTATION = true;
				LibConfiguration.USE_ANNOTATION_HANDLE = true;
				
				
				GlobalConfigurationService.getInstance().setFieldFormattingWithKoreanStyle( true );
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		return new Workable< View >(){
			@Override
			public void work( View tool ){
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "export some page annotation", new Runnable(){
					@Override
					public void run(){
						pdfView.exportPageAnnotationsToFDF( new int[]{ 1, 2, 3, 5, 6 }, "/sdcard/exp.fdf" );
					}
				} ) );
				mcs.add( new MenuCommand( "import fdf", new Runnable(){
					@Override
					public void run(){
						pdfView.getPDF().importFromFDF( true, true, "/sdcard/exp.fdf" );
						pdfView.onAnnotationsAllInvalidated();
					}
				} ) );
				WidgetFactory.uiPopupMenu( tool, mcs );
			}
		};
	}//method

	private PDFView pdfView;
	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		pdfView = getPDFView();
		pdfView.setPDFViewListener( new PDFViewAdapter(){
			@Override
			public void onOpenCompleted( PDFViewEvent e ){
				runOnUiThread( new Runnable(){
					@Override
					public void run(){
						pdfView.getToolbarService().uiForAnnotation();
					}
				} );
			}//method
		} );
		
		pdfView.openPDF( "/sdcard/sample.pdf", 0 );
		
		
	}//method

}//method
