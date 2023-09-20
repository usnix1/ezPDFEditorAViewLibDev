package udk.android.editor.test;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import udk.android.editor.env.LibConfiguration;
import udk.android.editor.lib.PDFLibrary;
import udk.android.editor.pdf.action.PlayableMediaInfo;
import udk.android.editor.view.pdf.GlobalConfigurationService;
import udk.android.editor.view.pdf.InteractionFilter;
import udk.android.editor.view.pdf.PDFView;
import udk.android.editor.view.pdf.PDFViewAdapter;
import udk.android.editor.view.pdf.PDFViewEvent;
import udk.android.util.AssignChecker;
import udk.android.util.LogUtil;
import udk.android.util.Workable;
import udk.android.util.vo.menu.MenuCommand;
import udk.android.widget.Alerter;
import udk.android.widget.WidgetFactory;

/**
 * 
 * @author JEON YONGTAE
 */
public class TestReadingAdventure extends TestBase{

	@Override
	public Runnable getAdditionalConfiguration(){
		return new Runnable(){
			@Override
			public void run(){
				
				LibConfiguration.DEBUGDRAW = false;
				
				LibConfiguration.OPEN_METHOD_NO_CACHING = false;
				LibConfiguration.USE_EBOOK_MODE = true;
				LibConfiguration.EBOOK_MODE_MAGAZINE_LAYOUT = true;
				LibConfiguration.ZOOM_MIN_DEFAULT_METHOD = LibConfiguration.ZOOM_MIN_DEFAULT_METHOD_AUTOFIT_IMAGEFULL;
				
				LibConfiguration.USE_DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.DOUBLE_PAGE_VIEWING = true;
				LibConfiguration.USE_QUIZ = true;
				
				GlobalConfigurationService.getInstance().setDefaultBackgroundColor24( 0xffffff );
				LibConfiguration.AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = true;
				LibConfiguration.PROGRESSIVE_IN_AUDIO_PLAY_WITH_TEXT_HIGHLIGHT = false;
				
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
				mcs.add( new MenuCommand( "다음페이지", new Runnable(){
					@Override
					public void run(){
						pdfView.nextPage();
					}//method
				} ) );
				mcs.add( new MenuCommand( "미디어 - 자동재생", new Runnable(){
					@Override
					public void run(){
						if( pdfView.isMediaActivated() ){
							pdfView.mediaStop();
						}//if
						pdfView.uiPlayAllMultimedia( context, new Runnable(){
							@Override
							public void run(){
								Alerter.shortNotice( context, "끝!!" );
							}//method
						} );
					}//method
				} ) );
				mcs.add( new MenuCommand( "미디어 - Pause", new Runnable(){
					@Override
					public void run(){
						if( pdfView.isMediaActivatedAsPlaying() ){
							pdfView.mediaPause();
						}//if
					}//method
				} ) );
				mcs.add( new MenuCommand( "미디어 - Resume", new Runnable(){
					@Override
					public void run(){
						if( pdfView.isMediaActivatedAsPausing() ){
							pdfView.mediaResume();
						}//if
					}//method
				} ) );
				mcs.add( new MenuCommand( "미디어 - Stop", new Runnable(){
					@Override
					public void run(){
						if( pdfView.isMediaActivated() ){
							pdfView.mediaStop();
						}//if
					}//method
				} ) );
				mcs.add( new MenuCommand( "워드블락", new Runnable(){
					@Override
					public void run(){
						int[] pages = new int[]{ pdfView.getPage(), pdfView.getOtherPageInDoublePageView( pdfView.getPage() ) };
						for( int page : pages ){
							
							float zoom = pdfView.getZoom();
							List< String > ids = pdfView.getAnnotationQuizWordBlockIds( page );
							if( AssignChecker.isAssigned( ids ) ){
								for( String uid : ids ){
									RectF annotBounds = pdfView.getAnnotationBounds( page, uid, zoom );			
									String blockStr = pdfView.getTextForAnnotationQuizWordBlock( page, uid );
									RectF blockBounds = pdfView.getTextBlockBoundsForAnntotaionQuizWordBlock( page, uid, zoom );
									Alerter.shortNotice( context, "page " + page + " : " + blockStr + " - "+ blockBounds + " - " + annotBounds );
								}//for
							}else{
								Alerter.shortNotice( context, "page " + page + " : 없당" );
							}//if
							
						}//for
					}//method
				} ) );
				mcs.add( new MenuCommand( "워드블락 - 노뷰", new Runnable(){
					@Override
					public void run(){
						try{
							PDFLibrary pdfLib = PDFLibrary.getInstance();
							pdfLib.openCSP( context, "/sdcard/ra_lv1_p2_c009_Where Is Fluffy the Rabbit.pdf" );
						
							for( int page = 1; page <= pdfLib.getPageCount(); page++ ){
								
								float zoom = 1;
								List< String > ids = pdfLib.getAnnotationQuizWordBlockIds( page );
								if( AssignChecker.isAssigned( ids ) ){
									for( String uid : ids ){
										RectF annotBounds = pdfLib.getAnnotationBounds( page, uid, zoom );			
										String blockStr = pdfLib.getTextForAnnotationQuizWordBlock( page, uid );
										RectF blockBounds = pdfLib.getTextBlockBoundsForAnntotaionQuizWordBlock( page, uid, zoom );
										Alerter.shortNotice( context, "page " + page + " : " + blockStr + " - "+ blockBounds + " - " + annotBounds );
									}//for
								}else{
									Alerter.shortNotice( context, "page " + page + " : 없당" );
								}//if
								
							}//for
							
							pdfLib.close();
							
						}catch( Exception ex ){
							LogUtil.e( ex );
						}//try
					}//method
				} ) );				

				WidgetFactory.uiPopupMenu( v, mcs );

			}//method
		};
	}//method
	
	private PlayableMediaInfo[] mediaList;
	private PDFView pdfView;

	@Override
	public void onCreate( Bundle savedInstanceState ){
		
		super.onCreate( savedInstanceState );
		
		final Context context = this;
		
		pdfView = getPDFView();
		pdfView.setOnViewPageChangeListener( new PDFView.OnViewPageChangeListener(){
			@Override
			public void onViewPageChange( int page ){
				
				Alerter.shortNotice( context, "View Page Changed : " + page );

			}
		} );
		
		boolean testOutControl = true;
		
		if( testOutControl ){
			
			pdfView.setInteractionFilter( new InteractionFilter(){
				@Override
				public boolean onTouchEvent( MotionEvent e ){
					if( e.getAction() == MotionEvent.ACTION_UP ){
						
						pdfView.checkAndActivateCurrentPageInDoublePageView( e.getX() );
						
						boolean consume = false;
						
						if( mediaList != null ){
							int page = pdfView.getPage();
							float zoom = pdfView.getZoom();
							PointF pt = pdfView.convertViewPositionToPagePositionIfOrNotDoublePageViewing( new PointF( e.getX(), e.getY() ) );
							
							for( int i = 0; i < mediaList.length; i++ ){
								PlayableMediaInfo pmi = mediaList[ i ];
								if( pmi.hitTest( pdfView, page, zoom, pt ) ){
									Alerter.shortNotice( context, "hit" );
									consume = true;
									
									PlayableMediaInfo[] play = null;
									boolean hittedAndAfter = false;
									if( !hittedAndAfter ){
										//히트된 미디어만 재생
										play = new PlayableMediaInfo[]{ pmi };
									}else{
										//히트된 미디어부터 줄줄이 재생
										play = new PlayableMediaInfo[ mediaList.length - i ];
										System.arraycopy( mediaList, i, play, 0, mediaList.length - i );
									}//if
									
									if( pdfView.isMediaActivated() ){
										pdfView.mediaStop();
									}//if
									pdfView.uiPlayMultimedia( context, play, pdfView.getPage(), pdfView.getZoom(), pt, new Runnable(){
										@Override
										public void run(){
											Alerter.shortNotice( context, "끝!" );
										}//method
									} );
									break;
								}//if

							}//for
						}//if
						
						if( !consume ){
							pdfView.simulateTapUpAnnotation( e, true );
						}
						
					}//if
					
					//PDFView 로 이벤트 넘어가지 않음
					return true;
				}//method
			} );
		
		}//if
		
		pdfView.setPDFViewListener( new PDFViewAdapter(){
			@Override
			public void onOpenCompleted( PDFViewEvent e ){
				pdfView.asyncGetMultimediaPlayList( new Workable< PlayableMediaInfo[] >(){
					@Override
					public void work( PlayableMediaInfo[] mediaList ){
						TestReadingAdventure.this.mediaList = mediaList;
					}//method
				} );
			}//method
		} );
//		pdfView.openPDF( "/sdcard/s.pdf", 0 );
//		pdfView.openPDF( "/sdcard/Compass_Publishing_Level_1_04_Come Down Whiskers - BG Animation.pdf", 0 );
//		pdfView.openPDF( "/sdcard/Popup-Slideshow-Test.pdf", 0 );
		pdfView.openPDF( "/sdcard/subtitle_sample.pdf", 0 );		
//		pdfView.openPDF( "/sdcard/Subtitle-Test.pdf.pdf", 0 );
//		pdfView.openPDF( "/sdcard/YLCR_L1-2_Hansel and Gretel.pdf", 0 );
//		pdfView.openPDF( "/sdcard/ra_lv1_p2_c009_Where Is Fluffy the Rabbit.pdf", 0 );
//		pdfView.openPDF( "/sdcard/ra_lv4_p5_c010_The_Water_Cycle.pdf", 5 );
//		pdfView.openPDF( "/sdcard/10.Come Down Whiskers.part1.content10.pdf", 0 );
		
	}//method

}//method
