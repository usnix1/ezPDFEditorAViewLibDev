package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.editor.ReaderAppContext;
import udk.android.editor.env.LibConfiguration;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestJavaScript extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.USE_FORM = true;
				LibConfiguration.USE_JAVASCRIPT = true;
				
			}//method
		};
	}//method

	@Override
	public Workable< View > getOnTest(){
		
		final Context context = this;
		
		return new Workable< View >(){
			@Override
			public void work( View v ){
				
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "JS Console", new Runnable(){
					@Override
					public void run(){
						ReaderAppContext.getInstance().getJavaScriptService().uiStartConsole( context );
					}//method
				} ) );

				WidgetFactory.uiPopupMenu( v, mcs );

			}//method
		};
	}//method
	
	private PDFView pdfView;

	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		pdfView = getPDFView();
		
//		pdfView.openPDF( "/sdcard/w2.pdf", 0 );
		pdfView.openPDF( "/sdcard/sample_b.pdf", 0 );
//		pdfView.openPDF( "/sdcard/event2.pdf", 0 );
		
	}//method

}//method
