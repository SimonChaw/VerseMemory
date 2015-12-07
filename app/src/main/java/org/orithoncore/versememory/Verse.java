package org.orithoncore.versememory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Simon on 12/6/2015.
 */
public class Verse implements Parcelable {
    String bookName;
    int chapterNum;
    String verseNum;
    int id;
    public Verse(String bookName, int chapterNum, String verseNum, int id) {
        this.bookName = bookName;
        this.chapterNum = chapterNum;
        this.verseNum = verseNum;
        this.id = id;
    }

    protected Verse(Parcel in) {
        bookName = in.readString();
        chapterNum = in.readInt();
        verseNum = in.readString();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookName);
        dest.writeInt(chapterNum);
        dest.writeString(verseNum);
        dest.writeInt(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Verse> CREATOR = new Parcelable.Creator<Verse>() {
        @Override
        public Verse createFromParcel(Parcel in) {
            return new Verse(in);
        }

        @Override
        public Verse[] newArray(int size) {
            return new Verse[size];
        }
    };
}
