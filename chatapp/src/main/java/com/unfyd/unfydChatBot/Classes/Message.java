package com.unfyd.unfydChatBot.Classes;

import java.util.List;

public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;
    public static final int TYPE_OPTIONS = 3;

    private int mType;
    private String mMessage;
    private String mUsername;
    private String mTypeValue;
    private String mOptionValue;

    private List<String> optionLst;
    private List<String> imageLst;
    private List<String> wordLst;
    private List<String> pdfLst;
    private List<String> excelLst;
    private List<String> pptLst;
    private List<String>audioLst;
    private List<String>videoLst;

    public List<String> getVideoLst() {
        return videoLst;
    }

    public void setVideoLst(List<String> videoLst) {
        this.videoLst = videoLst;
    }

    public List<String> getAudioLst() {
        return audioLst;
    }

    public void setAudioLst(List<String> audioLst) {
        this.audioLst = audioLst;
    }

    List<clsCarousel> carouselLst;

    public List<String> getPptLst() {
        return pptLst;
    }

    public void setPptLst(List<String> pptLst) {
        this.pptLst = pptLst;
    }

    public List<String> getExcelLst() {
        return excelLst;
    }

    public void setExcelLst(List<String> excelLst) {
        this.excelLst = excelLst;
    }

    public List<String> getWordLst() {
        return wordLst;
    }

    public List<String> getPdfLst() {
        return pdfLst;
    }

    public void setPdfLst(List<String> pdfLst) {
        this.pdfLst = pdfLst;
    }

    public void setWordLst(List<String> wordLst) {
        this.wordLst = wordLst;
    }

    public List<String> getImageLst() {
        return imageLst;
    }

    public void setImageLst(List<String> imageLst) {
        this.imageLst = imageLst;
    }

    private Message() {}

    public int getType() {
        return mType;
    };

    public String getMessage() {
        return mMessage;
    };
    public String getOptionValue() {
        return mOptionValue;
    };


    public String getUsername() {
        return mUsername;
    };
    public String getTypeValues() {
        return mTypeValue;
    };

    public List<String> getOptionLst(){return optionLst;};

    public List<clsCarousel> getcarouselLst(){return carouselLst;};


    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mTypeValue;
        private List<String> mOPtionlist;
        private List<clsCarousel> mCarouselLST;
        private List<String>mImageList;
        private List<String>mWordList;
        private List<String>mPdfList;
        private List<String>mExcelList;
        private List<String>mPPTList;
        private List<String>mAudioList;
        private List<String>mVideoList;


        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }
        public Builder option(List<String> message) {
            mOPtionlist = message;
            return this;
        }

        public Builder image(List<String> img) {
            mImageList = img;
            return this;
        }

        public Builder word(List<String> wor) {
            mWordList = wor;
            return this;
        }
        public Builder pdf(List<String> pdf) {
            mPdfList = pdf;
            return this;
        }
        public Builder excel(List<String> lstexcel) {
            mExcelList = lstexcel;
            return this;
        }
        public Builder ppt(List<String> lstppt) {
            mPPTList = lstppt;
            return this;
        }
        public Builder audio(List<String> lstaudio) {
            mAudioList = lstaudio;
            return this;
        }
        public Builder video(List<String> lstvideo) {
            mVideoList = lstvideo;
            return this;
        }
        public Builder carousel(List<clsCarousel> message) {
            mCarouselLST = message;
            return this;
        }
        public Builder typeValue(String typeValue) {
            mTypeValue = typeValue;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mTypeValue=mTypeValue;
            message.optionLst=mOPtionlist;
            message.carouselLst=mCarouselLST;
            message.imageLst=mImageList;
            message.wordLst=mWordList;
            message.pdfLst=mPdfList;
            message.excelLst=mExcelList;
            message.pptLst=mPPTList;
            message.audioLst=mAudioList;
            message.videoLst=mVideoList;
            return message;
        }
    }
}
