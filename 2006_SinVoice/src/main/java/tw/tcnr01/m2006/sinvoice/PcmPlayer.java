
package tw.tcnr01.m2006.sinvoice;

import android.media.AudioManager;
import android.media.AudioTrack;

import tw.tcnr01.m2006.sinvoice.Buffer.BufferData;

public class PcmPlayer {
    private final static String TAG = "PcmPlayer";
    private final static int STATE_START = 1;
    private final static int STATE_STOP = 2;

    private int mState;
    private AudioTrack mAudio;
    private long mPlayedLen;
    private Listener mListener;
    private Callback mCallback;

    public static interface Listener {
        void onPlayStart();

        void onPlayStop();
    }

    public static interface Callback {
        BufferData getPlayBuffer();

        void freePlayData(BufferData data);
    }

    public PcmPlayer(Callback callback, int sampleRate, int channel, int format, int bufferSize) {
        mCallback = callback;
        mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channel, format, bufferSize, AudioTrack.MODE_STREAM);
        mState = STATE_STOP;
        mPlayedLen = 0;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void start() {
        LogHelper.d(TAG, "start");
        if (STATE_STOP == mState && null != mAudio) {
            mPlayedLen = 0;

            if (null != mCallback) {
                mState = STATE_START;
                LogHelper.d(TAG, "start");
                if (null != mListener) {
                    mListener.onPlayStart();
                }
                while (STATE_START == mState) {
                    LogHelper.d(TAG, "start getbuffer");

                    BufferData data = mCallback.getPlayBuffer();
                    if (null != data) {
                        if (null != data.mData) {
                            int len = mAudio.write(data.mData, 0, data.getFilledSize());

                            if (0 == mPlayedLen) {
                                mAudio.play();
                            }
                            mPlayedLen += len;
                            mCallback.freePlayData(data);
                        } else {
                            // it is the end of input, so need stop
                            LogHelper.d(TAG, "it is the end of input, so need stop");
                            break;
                        }
                    } else {
                        LogHelper.e(TAG, "get null data");
                        break;
                    }
                }

                if (null != mAudio) {
                    mAudio.pause();
                    mAudio.flush();
                    mAudio.stop();
                }
                mState = STATE_STOP;
                if (null != mListener) {
                    mListener.onPlayStop();
                }
                LogHelper.d(TAG, "end");
            }
        }
    }

    public void stop() {
        if (STATE_START == mState && null != mAudio) {
            mState = STATE_STOP;
        }
    }
}
