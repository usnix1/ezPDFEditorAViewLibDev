package udk.android.editor.test;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import udk.android.editor.pdf.annotation.Annotation;
import udk.android.editor.view.pdf.AnnotationListView;
import udk.android.editor.view.pdf.AttachedFileListView;
import udk.android.editor.view.pdf.PDFMediaListView;
import udk.android.editor.view.pdf.PDFView;
import udk.android.util.StateObject;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestMediaList extends TestBase{
	
	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
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
				mcs.add( new MenuCommand( "첨부파일목록", new Runnable(){
					@Override
					public void run(){
						
						final StateObject< AttachedFileListView > aflv = new StateObject< AttachedFileListView >();
						aflv.value = new AttachedFileListView( context, pdfView.getPDF(), new Runnable(){
							@Override
							public void run(){
								getRootContainer().removeView( aflv.value );
							}//method
						} );
						aflv.value.setBackgroundColor( 0xffffffff );
						RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
						getRootContainer().addView( aflv.value, rlps );
						aflv.value.uiActivate();
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "주석목록", new Runnable(){
					@Override
					public void run(){
						
						final StateObject< AnnotationListView > alv = new StateObject< AnnotationListView >();
						alv.value = new AnnotationListView( 
										context, 
										pdfView.getPDF(), 
										new Workable< Annotation >(){
											@Override
											public void work( Annotation tool ){
												
											}//method
										}, 
										new Runnable(){
											@Override
											public void run(){
												getRootContainer().removeView( alv.value );
											}//method
										} );
						alv.value.setBackgroundColor( 0xffffffff );
						RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
						getRootContainer().addView( alv.value, rlps );
						alv.value.uiActivate();
						
					}//method
				} ) );
				mcs.add( new MenuCommand( "미디어목록", new Runnable(){
					@Override
					public void run(){
						
						final StateObject< PDFMediaListView > alv = new StateObject< PDFMediaListView >();
						alv.value = new PDFMediaListView( 
										context, 
										pdfView, 
										new Runnable(){
											@Override
											public void run(){
												getRootContainer().removeView( alv.value );
											}//method
										} );
						alv.value.setBackgroundColor( 0xffffffff );
						RelativeLayout.LayoutParams rlps = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
						getRootContainer().addView( alv.value, rlps );
						
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
		
		pdfView.openPDF( "/sdcard/2.A Letter to Roberto.part2.content2.pdf", 0 );
//		pdfView.openPDF( "/sdcard/9780153513541.pdf", 0 );
//		pdfView.openPDF( "/sdcard/attachment.pdf", 0 );
//		pdfView.openPDF( "/sdcard/form_filled-1.pdf", 0 );
		
	}//method

}//method
