package com.example.david.minolovec;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class Tile extends Button
{
    private boolean isMine;
    private boolean isFlag;
    private boolean isQuestionMark;
    private boolean isCovered;
    private int noSurroundingMines;

    public Tile(Context context)
    {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public Tile(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setDefaults()
    {
        isMine = false;
        isFlag = false;
        isQuestionMark = false;
        isCovered = true;
        noSurroundingMines = 0;
        this.setBackgroundResource(R.drawable.polje_neodprto);
    }

    public boolean isQuestionMark()
    {
        return isQuestionMark;
    }

    /*public void setMine(boolean mine)
    {

    }*/

     public void setCoveredIcon()
     {
         isFlag = false;
         isQuestionMark = false;
         this.setBackgroundResource(R.drawable.polje_neodprto);
     }

    public void setFlagIcon()
    {
        this.setBackgroundResource(R.drawable.zastavica);
    }

    public void setQuestionMarkIcon()
    {
        this.setBackgroundResource(R.drawable.vprasaj);
    }

    public void setFlag()
    {
        isFlag = true;
        setFlagIcon();
    }

    public void setQuestionMark()
    {
        isQuestionMark = true;
        setQuestionMarkIcon();
    }

    public void setUncovered()
    {
        isCovered = false;
    }

    public void updateSurroundingMineCount()
    {
        noSurroundingMines++;
    }

    //set the tile as a mine
    public void plantMine()
    {
        isMine = true;
    }

    public boolean isMine()
    {
        return isMine;
    }

    public boolean isFlag()
    {
        return isFlag;
    }

    public int getNoSurroundingMines()
    {
        return noSurroundingMines;
    }

    public boolean isCovered()
    {
        if(!isCovered)
        {
            setUncovered();
        }
        return isCovered;
    }

    //uncover the tile
    public void openTile()
    {
        //isCovered = false;
        if(!isCovered)
        {
            // exita ven iz metode
            return;
        }
        setUncovered();
        if(this.isMine())
        {
            triggerMine();
        }
        else
        {
            showNumber();
        }
    }

    //show the number icon
    public void showNumber()
    {
        String img =  "polje_"+noSurroundingMines;
        int drawableId = getResources().getIdentifier(img, "drawable", "com.example.david.minolovec");
        this.setBackgroundResource(drawableId);
    }

    //show the mine icon
    public void triggerMine()
    {
        this.setBackgroundResource(R.drawable.mina_splosna);
    }
}