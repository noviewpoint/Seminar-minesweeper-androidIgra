package com.example.david.minolovec;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Random;


public class Game extends Activity
{
    String filename = "lestvica_";

    public static final String KEY_DIFFICULTY = "com.example.david.minolovec.difficulty";
    public static final int DIFFICULTY_EASY = 0;
    //public static final int DIFFICULTY_MEDIUM = 1;
    //public static final int DIFFICULTY_HARD = 2;

    public static int easyRows = 9;
    public static int easyColumns = 9;
    public static int easyMines = 10;

    public static int mediumRows = 16;
    public static int mediumColumns = 16;
    public static int mediumMines = 40;

    public static int hardRows = 30;
    public static int hardColumns = 16;
    public static int hardMines = 99;

    public Tile[][] tiles;

    public static int tileWH = 40;
    public static int tilePadding = 2;

    public TableLayout mineField;
    // gumb za restartanje
    public ImageButton imageButton;
    // gumb za alternative play
    public ImageButton imageButton2;

    public int totalRows;
    public int totalCols;
    public int totalMines;

    public boolean timerStarted = false;
    public boolean minesSet = false;

    public Handler timer = new Handler();
    public int secondsPassed = 0;
    public TextView timerText;
    public TextView minesText;
    public LinearLayout linearni;

    public int mineCount;

    public boolean alternativePlay = false;
    public boolean zaklenjenaIgra = false;

    public int correctFlags;
    public int totalCoveredTiles;
    public int totalCoveredMines;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int diff = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        setContentView(R.layout.game);
        mineField = (TableLayout) findViewById(R.id.MineField);


        createGameBoard(diff);
        showGameBoard();

        final int newFinalDifficultyVar = diff;

        imageButton=(ImageButton)findViewById(R.id.smiley);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGame();
                createGameBoard(newFinalDifficultyVar);
                showGameBoard();
                imageButton.setImageResource(R.drawable.smiley_play);

                // default tekst za casovnik in stevec min
                minesText.setText("" + mineCount);
                timerText.setText("000");
            }
        });

        /*imageButton2 =(ImageButton)findViewById(R.id.alternative);
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alternativePlay) {
                    alternativePlay = false;
                }
                else
                {
                    alternativePlay = true;
                }
            }
        });*/

        timerText = (TextView) findViewById(R.id.timer);
        minesText = (TextView) findViewById(R.id.MineCount);
        //linearni = (LinearLayout)findViewById(R.id.linearni);
        timerText.setText("000");
        minesText.setText("" + mineCount);
        nastaviVelikostLayouta();
    }


    public void startTimer()
    {
        if(secondsPassed == 0)
        {
            Log.d("Game.java - startTImer()", "prisel v startTimer() funkcijo");
            timer.removeCallbacks(updateTimer);
            timer.postDelayed(updateTimer, 1000);
        }
    }

    public void stopTimer()
    {
        timer.removeCallbacks(updateTimer);
    }

    private Runnable updateTimer = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;
            String curTime = Integer.toString(secondsPassed);
            Log.d("Pretecen cas je", curTime);

            //update the text view
            if (secondsPassed < 10)
            {
                timerText.setText("00" + curTime);
            }
            else if (secondsPassed < 100)
            {
                timerText.setText("0" + curTime);
            }
            else
            {
                timerText.setText(curTime);
            }
            timer.postAtTime(this, currentMilliseconds);
            //run again in 1 second
            timer.postDelayed(updateTimer, 1000);
        }
    };

    public void showGameBoard()
    {
        //for every row
        for(int row=0;row<totalRows;row++)
        {
            //create a new table row
            TableRow tableRow = new TableRow(this);
            //set the height and width of the row
            tableRow.setLayoutParams(new LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));

            //for every column
            for(int col=0;col<totalCols;col++)
            {
                //set the width and height of the tile
                tiles[row][col].setLayoutParams(new LayoutParams(tileWH * tilePadding,  tileWH * tilePadding));
                //add some padding to the tile
                tiles[row][col].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
                //add the tile to the table row
                tableRow.addView(tiles[row][col]);
            }
            //add the row to the minefield layout
            mineField.addView(tableRow,new TableLayout.LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));
        }
    }

    // ne dela!
    public void nastaviVelikostLayouta() {

        /*Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError err) {
            display.getSize(size);
        }
        int width = size.x;
        int height = size.y;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(width/3),
                LinearLayout.LayoutParams.WRAP_CONTENT); // or set height to any fixed value you want

        linearni.setLayoutParams(lp);*/

        //majhno polje
        if (totalMines > 10) {
            //float steviloDpSmall = 100 * getResources().getDisplayMetrics().density;
            //minesText.setWidth(Math.round(steviloDpSmall));
            //linearni.setMinimumWidth(500);
            Log.d("aaa", "spreminajm iz" + minesText.getWidth());
            minesText.setWidth(800);
            Log.d("aaa", "spreminajm na veliko" + minesText.getWidth());
        }
    }

    public void setupMineField(int row, int col)
    {
        Random random = new Random();
        int mineRow;
        int mineCol;
        for(int i = 0; i < totalMines; i++)
        {
            mineRow = random.nextInt(totalRows);
            mineCol = random.nextInt(totalCols);

            if(mineRow == row && mineCol == col) //cicked tile
            {
                i--;
            }
            else if(tiles[mineRow][mineCol].isMine()) //already a mine
            {
                i--;
            }
            else
            {
                //plant a new mine
                tiles[mineRow][mineCol].plantMine();
                //go one row and col back
                int startRow = mineRow-1;
                int startCol = mineCol-1;
                //check 3 rows across and 3 down
                int checkRows = 3;
                int checkCols = 3;
                if(startRow < 0) //if it is on the first row
                {
                    startRow = 0;
                    checkRows = 2;
                }
                else if(startRow+3 > totalRows) //if it is on the last row
                    checkRows = 2;

                if(startCol < 0)
                {
                    startCol = 0;
                    checkCols = 2;
                }
                else if(startCol+3 > totalCols) //if it is on the last row
                    checkCols = 2;

                for(int j=startRow;j<startRow+checkRows;j++) //3 rows across
                {
                    for(int k=startCol;k<startCol+checkCols;k++) //3 rows down
                    {
                        if(!tiles[j][k].isMine()) //if it isn't a mine
                            tiles[j][k].updateSurroundingMineCount();
                    }
                }
            }
        }
    }


    public boolean checkWonGame()
    {
        if(totalCoveredTiles == totalMines || correctFlags == totalMines)
            return true;
        else
            return false;
    }



    public void uncoverTiles(int row, int col)
    {
        //if the tile is a mine, or a flag return
        if(tiles[row][col].isMine() || tiles[row][col].isFlag())
            return;

        tiles[row][col].openTile();

        totalCoveredTiles--;

        if(tiles[row][col].getNoSurroundingMines() > 0)
            return;

        //go one row and col back
        int startRow = row-1;
        int startCol = col-1;
        //check 3 rows across and 3 down
        int checkRows = 3;
        int checkCols = 3;
        if(startRow < 0) //if it is on the first row
        {
            startRow = 0;
            checkRows = 2;
        }
        else if(startRow+3 > totalRows) //if it is on the last row
            checkRows = 2;

        if(startCol < 0)
        {
            startCol = 0;
            checkCols = 2;
        }
        else if(startCol+3 > totalCols) //if it is on the last row
            checkCols = 2;

        for(int i=startRow;i<startRow+checkRows;i++) //3 or 2 rows across
        {
            for(int j=startCol;j<startCol+checkCols;j++) //3 or 2 rows down
            {
                if(tiles[i][j].isCovered())
                    uncoverTiles(i,j);
            }
        }
    }


    public void loseGame()
    {
        imageButton.setImageResource(R.drawable.smiley_defeat);
        endGame();
        String x = this.getString(R.string.luzerTekst);
        Toast.makeText(getApplicationContext(), x,
                Toast.LENGTH_SHORT).show();



        /* ----- za testiranje kako delujejo highscores lahko rezultate vpisujemo tudi ob porazih

        posodobiHighscores();

         */
    }


    public void winGame()
    {
        imageButton.setImageResource(R.drawable.smiley_win);
        endGame();
        posodobiHighscores();
    }


    public void resetGame()
    {
        // remove the table rows from the minefield table layout
        mineField.removeAllViews();

        // reset variables
        timerStarted = false;
        minesSet = false;
        secondsPassed = 0;
        correctFlags = 0;
        zaklenjenaIgra = false;

        stopTimer();
    }

    public void endGame()
    {
        stopTimer();
        zaklenjenaIgra = true;

        for(int i=0;i<totalRows;i++)
        {
            for(int j=0;j<totalCols;j++)
            {
                //if the tile is covered
                if(tiles[i][j].isCovered())
                {
                    //if there is no flag or mine
                    if(!tiles[i][j].isFlag() && !tiles[i][j].isMine())
                    {
                        tiles[i][j].openTile();
                    }
                    //if there is a mine but no flag
                    else if(tiles[i][j].isMine() && !tiles[i][j].isFlag())
                    {
                        tiles[i][j].openTile();
                    }

                }
            }
        }
    }

    public void setMineText(int x)
    {
        minesText.setText("" + x);
    }

    public void createGameBoard(int diff)
    {
        //set total rows and columns based on the difficulty
        totalRows = easyRows;
        totalCols = easyColumns;
        totalMines = easyMines;
        switch(diff)
        {
            case 0:
                break;
            case 1:
                totalRows = mediumRows;
                totalCols = mediumColumns;
                totalMines = mediumMines;
                break;
            case 2:
                totalRows = hardRows;
                totalCols = hardColumns;
                totalMines = hardMines;
                break;
        }
        mineCount = totalMines;
        totalCoveredMines = totalRows * totalCols;

        //setup the tiles array
        tiles = new Tile[totalRows][totalCols];

        for(int row = 0; row < totalRows;row++)
        {
            for(int col = 0; col < totalCols;col++)
            {
                //create a tile
                tiles[row][col] = new Tile(this);
                //set the tile defaults
                tiles[row][col].setDefaults();

                final int curRow = row;
                final int curCol = col;

                //add a click listener
                tiles[row][col].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(!timerStarted)
                        {
                            timerStarted = true;
                            startTimer();
                            setupMineField(curRow, curCol);
                        }
                        if(!minesSet)
                        {
                            minesSet = true;
                            setMineText(mineCount);
                        }

                        if(!tiles[curRow][curCol].isFlag() && !zaklenjenaIgra)
                        {
                            if(tiles[curRow][curCol].isMine())
                            {
                                tiles[curRow][curCol].openTile();
                                loseGame();
                            }
                            else
                            {
                                uncoverTiles(curRow, curCol);
                                imageButton.setImageResource(R.drawable.smiley_play);
                            }

                            if(checkWonGame())
                            {
                                winGame();
                            }
                        }

                        /*if(alternativePlay)
                        {
                            if(tiles[curRow][curCol].isCovered() && !zaklenjenaIgra)
                            {
                                if (!tiles[curRow][curCol].isFlag() && !tiles[curRow][curCol].isQuestionMark())
                                {
                                    tiles[curRow][curCol].setFlag();
                                    mineCount--;
                                    setMineText(mineCount);
                                }
                                else if(tiles[curRow][curCol].isFlag() && !tiles[curRow][curCol].isQuestionMark())
                                {
                                    tiles[curRow][curCol].setQuestionMark();
                                } else {
                                    tiles[curRow][curCol].setCoveredIcon();
                                }
                            }

                        }*/
                    }
                });

                //add a long click listener
                tiles[row][col].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        if (tiles[curRow][curCol].isCovered() && !zaklenjenaIgra)
                        {
                            if (!tiles[curRow][curCol].isFlag() && !tiles[curRow][curCol].isQuestionMark()) {
                                tiles[curRow][curCol].setFlag();
                                imageButton.setImageResource(R.drawable.smiley_zastavica);
                                mineCount--;
                                setMineText(mineCount);

                                if(tiles[curRow][curCol].isMine())
                                {
                                    correctFlags++;
                                }

                            } else if (tiles[curRow][curCol].isFlag() && !tiles[curRow][curCol].isQuestionMark()) {
                                tiles[curRow][curCol].setQuestionMark();
                                imageButton.setImageResource(R.drawable.smiley_vprasaj);
                                mineCount++;
                                setMineText(mineCount);

                                if(tiles[curRow][curCol].isMine())
                                {
                                    correctFlags--;
                                }
                            } else {
                                tiles[curRow][curCol].setCoveredIcon();
                                imageButton.setImageResource(R.drawable.smiley_play);
                            }

                            if(checkWonGame())
                            {
                                winGame();
                            }
                        }

                        return true;
                    }
                });
            }
        }
    }

    public void posodobiHighscores() {

        String vsebina = beriIzDatoteke(filename + totalMines);
        if (vsebina.isEmpty())
        {
            vsebina = "" + secondsPassed;
        }
        else
        {
            vsebina = vsebina + "," + secondsPassed;
        }

        String[] numberStrings = vsebina.split(",");
        int[] numberInts = new int[numberStrings.length];

        for(int i = 0; i < numberStrings.length; i++)
        {
            // Note that this is assuming valid input
            // If you want to check then add a try/catch
            // and another index for the numbers if to continue adding the others
            numberInts[i] = Integer.parseInt(numberStrings[i]);
        }

        // sortira po vrstnem redu
        Arrays.sort(numberInts);
        int vrstniRed;
        String tekstObHighscore;

        tekstObHighscore = tekstObHighscore(numberInts);

        vsebina = "";

        for(int i = 0; i < numberInts.length; i++)
        {
            if (i == 0)
            {
                vsebina += numberInts[i];
            }
            else
            {
                vsebina += "," + numberInts[i];
            }
        }

        // vpise sortirane case v file
        vpisiVDatoteko(vsebina, filename + totalMines);
        String x = this.getString(R.string.zmagovalniTekstSumniki);
        Toast.makeText(getApplicationContext(), x + " " + tekstObHighscore,
                Toast.LENGTH_LONG).show();
    }

    public String tekstObHighscore(int[] vhod) {
        for(int i = 0; i < vhod.length; i++)
        {
            if (secondsPassed <= vhod[i]) {
                return "Dosegel si " + (i+1) + ". mesto!";
            }
        }
        return "";
    }

    private void vpisiVDatoteko(String vsebina, String ime){
        try {
            // TOK ker je STREAM, ne vemo st. znakov ki bo vletelo
            //ustvarimo izhodni tok
            FileOutputStream os = openFileOutput(ime, Context.MODE_PRIVATE);
            //zapisemo posredovano vsebino v datoteko
            os.write(vsebina.getBytes());
            //sprostimo izhodni tok
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String beriIzDatoteke(String ime){

        // ustvarimo vhodni podatkovni tok
        FileInputStream inputStream;

        //ugotovimo, koliko je velika datoteka
        File file = new File(getFilesDir(), ime);
        int length = (int) file.length();

        //pripravimo spremenljivko, v katero se bodo prebrali podatki
        byte[] bytes = new byte[length];

        //preberemo podatke
        try {
            inputStream = openFileInput(ime);
            inputStream.read(bytes);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //podatke pretvorimo iz polja bajtov v znakovni niz
        String vsebina = new String(bytes);

        return vsebina;
    }

}

