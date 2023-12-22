package udk.android.editor.test;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.reader.pdf.selection.RectangleSelection;
import udk.android.reader.view.pdf.PDFView;
import udk.android.reader.view.pdf.PDFViewEvent;
import udk.android.reader.view.pdf.PDFViewListener;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestFormFieldHandleLongPress extends TestBase{
	
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
		return new Workable< View >(){
			@Override
			public void work( View v ){
				
				List< MenuCommand > mcs = new ArrayList< MenuCommand >();
				mcs.add( new MenuCommand( "TextBox", new Runnable(){
					@Override
					public void run(){
						getPDFView().addAnnotationTextBoxStart();
					}//method
				} ) );
				mcs.add( new MenuCommand( "TypeWriter", new Runnable(){
					@Override
					public void run(){
						getPDFView().addAnnotationTypeWriterStart();
					}//method
				} ) );
				WidgetFactory.uiPopupMenu( v, mcs );

			}//method
		};
	}//method

	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		final PDFView pdfView = getPDFView();
		pdfView.setPDFViewListener( new PDFViewListener(){
			
			@Override
			public void onLongPress( PDFViewEvent e ){
				
				MotionEvent me = e.motionEvent;
				PointF pt = pdfView.convertViewPositionToPagePosition( new PointF( me.getX(), me.getY() ) );
				String[] titles = pdfView.getFormFieldTitles();
				
				String hitted = null;
				
				if( titles != null && titles.length > 0 ){
					check : for( String title : titles ){
					
						List< RectangleSelection > rss = pdfView.getFormFieldBounds( title );
						if( rss != null && rss.size() > 0 ){
							
							for( RectangleSelection rs : rss ){
								
								if( rs.hitTest( pdfView.getZoom(), pt.x, pt.y ) ){
									hitted = title;
									break check;
								}//if
								
							}//for
							
						}//if
						
					}//for
				}//if
				
				if( hitted != null && "TextField".equals( pdfView.getFormFieldTypeName( hitted ) ) ){
					
					//TODO 붙여넣을 텍스트를 가져오는 로직은 직접 구현하셔야 합니다.
					String copiedText = "ok";
					pdfView.setFormFieldValue( hitted, copiedText );

				}//if
				
			}//method
			@Override
			public void onZoomChanged( PDFViewEvent e ){}
			@Override
			public void onViewUpdated( PDFViewEvent e ){}
			@Override
			public void onSingleTapUp( PDFViewEvent e ){}
			@Override
			public void onSavedAs( PDFViewEvent e ){}
			@Override
			public void onPageChanged( PDFViewEvent e ){}
			@Override
			public void onOpenCompleted( PDFViewEvent e ){}
			@Override
			public void onLinkTapped( PDFViewEvent e ){}
			@Override
			public void onDoubleTapUp( PDFViewEvent e ){}
			@Override
			public void onColumnFitDeactivated( PDFViewEvent e ){}
			@Override
			public void onColumnFitActivated( PDFViewEvent e ){}
		} );
		
		getPDFView().openPDF( "/sdcard/form_filled-1.pdf", 0 );
		
	}//method

}//method
