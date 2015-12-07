package org.orithoncore.versememory;

/**
 * Created by Simon on 12/6/2015.
 */
public class Verse {
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
}
