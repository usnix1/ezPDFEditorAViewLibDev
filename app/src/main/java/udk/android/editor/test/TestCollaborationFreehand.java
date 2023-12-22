package udk.android.editor.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import udk.android.reader.env.LibConfiguration;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.WidgetFactory;

/**
 * @author JEON YONGTAE
 *
 */
public class TestCollaborationFreehand extends Activity{
	@Override
	protected void onCreate( Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		
		LogUtil.DEBUG = true;
		
		LibConfiguration.USE_ANNOTATION = true;
		LibConfiguration.USE_ANNOTATION_CREATE_FREEHAND = true;
		LibConfiguration.USE_ANNOTATION_HANDLE = true;
		
		final Context context = this;
		
		Display d = getWindowManager().getDefaultDisplay();
		
		final PDFView[] pdfViews = new PDFView[ 2 ];
		for( int i = 0; i < pdfViews.length; i++ ){
			
			final PDFView pdfView = new PDFView( context );
			
			final int thisIdx = i;
			
			final Workable< String > updateInOtherView = new Workable< String >(){
				@Override
				public void work( String xfdf ){
					PDFView other = pdfViews[ thisIdx == 0 ? 1 : 0 ];
					try{
						other.importXFDF( xfdf.getBytes( "UTF-8" ) );
					}catch( Exception ex ){
						LogUtil.e( ex );
					}
				}//method
			};
			
			pdfView.setOnAnnotationFreehandCreatingListener( new PDFView.OnAnnotationFreehandCreatingListener(){

				@Override
				public void onStart( int page, String uid ){
					
				}//method
				
				@Override
				public void onUndo( String xfdf ){
					updateInOtherView.work( xfdf );
				}
				
				@Override
				public void onRedo( String xfdf ){
					updateInOtherView.work( xfdf );
				}
				
				@Override
				public void onNewLineStart(){
					
				}//method
				
				@Override
				public void onNewLine( String xfdf ){
					updateInOtherView.work( xfdf );
				}
				
				@Override
				public void onEndConfirm( String xfdf ){
					updateInOtherView.work( xfdf );
				}
				
				@Override
				public void onEndCancel( int page, String uid ){
					PDFView other = pdfViews[ thisIdx == 0 ? 1 : 0 ];
					other.deleteAnnotation( page, uid );
				}//method
				
			} );
			pdfViews[ i ] = pdfView;
		}//for
		
		LinearLayout l = new LinearLayout( context );
		l.setOrientation( d.getWidth() > d.getHeight() ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL );
		for( final PDFView pdfView : pdfViews ){
			LinearLayout l2 = new LinearLayout( context );
			l2.setOrientation( LinearLayout.VERTICAL );
			
			LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );
			lps.weight = 1;
			l.addView( l2, lps );
			
				final Button btn = new Button( context );
				btn.setText( "동작" );
				btn.setOnClickListener( new View.OnClickListener(){
					@Override
					public void onClick( View v ){
						List< MenuCommand > mcs = new ArrayList< MenuCommand >();
						if( pdfView.isNowCreatingAnnotationFreehand() ){
							
							mcs.add( new MenuCommand( "그리기 완료", new Runnable(){
								
								@Override
								public void run(){
									pdfView.addAnnotationFreehandEndConfirm();
								}//method
								
							} ) );
							mcs.add( new MenuCommand( "그리기 취소", new Runnable(){
								
								@Override
								public void run(){
									pdfView.addAnnotationFreehandEndCancel();
								}//method
								
							} ) );
							
						}else{
						
							mcs.add( new MenuCommand( "그리기 시작", new Runnable(){
								
								@Override
								public void run(){
									pdfView.addAnnotationFreehandStart( true );
								}//method
								
							} ) );
						}//if
						WidgetFactory.uiPopupMenu( btn, mcs );
					}
				} );
				lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
				lps.weight = 0;
				l2.addView( btn, lps );
				
				lps = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT );
				lps.weight = 0;
				l2.addView( pdfView, lps );
				pdfView.openPDF( "/sdcard/sample.pdf", 1 );
		}//for
		
		setContentView( l );
		
	}//method
}
