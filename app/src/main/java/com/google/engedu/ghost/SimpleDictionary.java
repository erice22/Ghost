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

import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix == "" || prefix == null){
            int rand = (int)(Math.random()*words.size())+1;
            return words.get(rand);
        }
        else{
            int foundWord = binarySearch(prefix);
            if(foundWord > -1){
                return words.get(foundWord);
            }
            else{
                return null;
            }
        }
    }

    int binarySearch(String word) {
        String subString = "";
        int low = 0, high = words.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (words.get(mid).length() >= word.length()) {
                subString = words.get(mid).substring(0, word.length());
                System.out.println(subString);
                // Check if x is present at mid
                if (subString.equals(word))
                    return mid;

                // If x greater, ignore left half
                if (subString.compareTo(word) < 0)
                    low = mid + 1;

                    // If x is smaller, ignore right half
                else
                    high = mid - 1;
            } else {
                subString = words.get(mid);
                String wordToCompare = word.substring(0, subString.length());
                if (subString.compareTo(wordToCompare) < 0)
                    low = mid + 1;

                    // If x is smaller, ignore right half
                else
                    high = mid - 1;
            }
        }

            // if we reach here, then element was
            // not present
            return -1;
    }


    @Override
    public String getGoodWordStartingWith(String prefix) {
        if(prefix == "" || prefix == null){
            int rand = (int)(Math.random()*words.size())+1;
            return words.get(rand);
        }
        else{
            int foundWord = binarySearch(prefix);
            if(foundWord > -1){
                int start = foundWord;
                while((words.get(start).length()>= prefix.length()) &&
                        words.get(start).substring(0, prefix.length()).equals(prefix)){
                    start--;
                }
                start += 1;
                int end = foundWord;
                while((words.get(end).length()>= prefix.length()) &&
                        words.get(end).substring(0, prefix.length()).equals(prefix)){
                    end++;
                }
                end -= 1;
                if(end == start){
                    return words.get(end);
                }
                ArrayList<String> oddWords = new ArrayList<String>();
                ArrayList<String> evenWords = new ArrayList<String>();
                for(int i = start;  i <= end; i++){
                    if(words.get(i).length() % 2 == 0){
                        evenWords.add(words.get(i));
                    }
                    else{
                        oddWords.add(words.get(i));
                    }
                }
                if(prefix.length() % 2 == 0 && (!oddWords.isEmpty())){
                    int random = (int)(Math.random() * oddWords.size());
                    System.out.println(oddWords.get(random));
                    return oddWords.get(random);
                }
                else{
                    int random = (int)(Math.random() * evenWords.size());
                    System.out.println(evenWords.get(random));
                    return evenWords.get(random);
                }
            }
            else{
                return null;
            }
        }
    }
}
