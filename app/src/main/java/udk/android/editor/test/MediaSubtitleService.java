package udk.android.editor.test;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import udk.android.reader.pdf.action.MediaSubtitleInfo;
import udk.android.reader.pdf.action.PlayableMediaInfo;
import udk.android.reader.view.pdf.PDFView;
import udk.android.util.ThreadUtil;
import udk.android.util.Workable;

public class MediaSubtitleService implements PDFView.MediaPlayStateListener {

    PDFView pdfView;
    ImageView layout;

    PlayableMediaInfo[] mediaList;
    int idxPlay = -1;



    public MediaSubtitleService(PDFView pdfView, ImageView layout){
        this.pdfView = pdfView;
        this.layout = layout;
        pdfView.setMediaPlayStateChangeListener(this);
    }

    public void init(){
        pdfView.asyncGetMultimediaSubtitleList( new Workable< PlayableMediaInfo[] >(){
            @Override
            public void work( PlayableMediaInfo[] mediaList ){
                MediaSubtitleService.this.mediaList = mediaList;
            }//method
        } );
    }

    public void playMedia( int index ){
        int page = pdfView.getPage();

        for( int i = 0; i < mediaList.length -1 ; i ++ ){
            if( mediaList[i].getPage() == pdfView.getPage() ){
                index = i;
                break;
            }
        }

        idxPlay = index;
        makeBackgroundImage(index);
        pdfView.uiPlayMultimedia(pdfView.getContext(), new PlayableMediaInfo[]{mediaList[index]}, new Runnable(){
            @Override
            public void run(){

            }
        });
    }

    private void onSubtitleChanged( final MediaSubtitleInfo play ){
        pdfView.post(new Runnable() {
            @Override
            public void run() {
            Toast.makeText(pdfView.getContext(), "TEXT : [" + play.getText() +"] with " + play.getColor(), Toast.LENGTH_SHORT).show();
            }
        });
//        Log.e("XXXXX", "Start:" + play.getRangeFrom() + ",End:" + play.getRangeTo() + ",TEXT:[" + play.getText() +"]");
    }

    private void makeBackgroundImage( int index){
        PlayableMediaInfo info = mediaList[index];
        int bgRefNo = info.getQuizSubtitleBackgroundRefNo();
        if( bgRefNo <= 0)
            return;

        int page = info.getPage();
        int bgColor = pdfView.getBackgroundColorPageInfo();
        float zoom = pdfView.getZoom();
        try {
            Bitmap background = pdfView.thumbnail(page, bgRefNo, (int) pdfView.getPageWidth(page, zoom) );
                layout.setImageBitmap(background);
        } catch (Exception e) {

        }

    }

    @Override
    public void onActivate(boolean fromUserTouch) {
        Thread t = new Thread(){
            @Override
            public void run(){
                List< MediaSubtitleInfo > subtitles = mediaList[idxPlay].getSubtitleInfos();

                if( subtitles == null || subtitles.size() <= 0 )
                    return;
                MediaSubtitleInfo nowPlayingMediaSubtitleInfo = null;
                MediaSubtitleInfo msiNow = null;

                while( pdfView.isMediaActivated() ){
                    long position = pdfView.getMediaCurrentPosition();
                    for( MediaSubtitleInfo info : subtitles ){
                        if( info.getRangeFrom() < position && position < info.getRangeTo() ) {
                            msiNow = info;
                            continue;
                        }
                    }
                    if( msiNow != null ) {
                        boolean isFirstPlayOfSubtitle = nowPlayingMediaSubtitleInfo != msiNow;
                        nowPlayingMediaSubtitleInfo = msiNow;

                        if( isFirstPlayOfSubtitle ){
                            onSubtitleChanged(msiNow);
                        }
                    }
                    ThreadUtil.sleepQuietly( 100 );
                }//while
            }//method
        };
        t.start();
    }

    @Override
    public void onDeactivate() {

    }

    @Override
    public void onPlayStateChanged() {

    }
}
