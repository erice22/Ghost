/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private TextView letters;
    private String characters = "";
    private TextView gameStatus;
    private TextView userScore;
    private TextView computerScore;
    private int computerScoreInt = 0;
    private int userScoreInt = 0;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        System.out.println("Start loading dictionary");
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new FastDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        letters = findViewById(R.id.ghostText);
        gameStatus = findViewById(R.id.gameStatus);
        userScore = findViewById(R.id.your_score);
        computerScore = findViewById(R.id.computer_score);
        System.out.println("check");
        System.out.println(dictionary.isWord("bean"));
        onStart(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString("message", (String)gameStatus.getText());
        outState.putString("enteredLetters", (String)letters.getText());
        outState.putInt("compScore", computerScoreInt);
        outState.putInt("userScore", userScoreInt);
        System.out.println("saving state");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        if(savedInstanceState.getString("message") != null){
            gameStatus.setText(savedInstanceState.getString("message"));
            System.out.println("restoring message");
        }
        if(savedInstanceState.getString("enteredLetters") != null){
            letters.setText(savedInstanceState.getString("enteredLetters"));
            characters = savedInstanceState.getString("enteredLetters");
            System.out.println("restoring letters");
        }
        computerScoreInt = savedInstanceState.getInt("compScore");
        userScoreInt = savedInstanceState.getInt("userScore");
        computerScore.setText("Computer: " + computerScoreInt);
        userScore.setText("User: " + userScoreInt);

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        if (userTurn) {
            gameStatus.setText(USER_TURN);
        } else {
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }


    public boolean computerTurn(){
        System.out.println(characters);
        String foundWord = dictionary.getGoodWordStartingWith(characters);
        System.out.println(foundWord);
        gameStatus.setText(COMPUTER_TURN);
        if((characters.length() >= 4) && (dictionary.isWord(characters))){
            gameStatus.setText("Computer Victory!");
            computerScoreInt += 1;
            System.out.println(computerScoreInt);
            computerScore.setText("Computer: " + computerScoreInt);
        }
        else if(foundWord == null || foundWord == ""){
            gameStatus.setText("Computer challenges: no possible word. Computer wins!");
            computerScoreInt+= 1;
            computerScore.setText("Computer: " + computerScoreInt);
        }
        else{
            int subString = characters.length();
            characters += foundWord.charAt(subString);
            letters.setText(characters);
            userTurn = true;
            gameStatus.setText( USER_TURN);
        }
        return true;
    }
    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int unicodeEntered = event.getUnicodeChar();
        char entered = (char)unicodeEntered;
        if(Character.isLetter(entered)){
            characters += entered;
            letters.setText(characters);
            computerTurn();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void challenge(View view){
        if(characters.length() >= 4 && dictionary.isWord(characters)){
            gameStatus.setText( "Challenge successful! " + characters + " is a vaild word. You win!");
            userScoreInt+= 1;
            userScore.setText("User: " + userScoreInt);
        }
        else{
            String validWord = dictionary.getAnyWordStartingWith(characters);
            if(validWord != null){
                gameStatus.setText("Challenge failed: " + validWord + " is a valid word");
                computerScoreInt+= 1;
                computerScore.setText("Computer: " + computerScoreInt);
            }
            else{
                gameStatus.setText("Challenge successful: no possible words can be formed. You win!");
                userScoreInt+= 1;
                userScore.setText("User: " + userScoreInt);
            }
        }
    }

    public void restart(View view){
        characters = "";
        letters.setText(characters);
        userTurn = random.nextBoolean();
    }
}
