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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
       TrieNode currentRoot = this;
       for(int i = 1; i <= s.length(); i++){
          if(currentRoot.children.isEmpty() || !(currentRoot.children.containsKey(s.substring(0, i)))){
             TrieNode newKids = new TrieNode();
             currentRoot.children.put(s.substring(0, i), newKids);
          }
          currentRoot = currentRoot.children.get(s.substring(0, i));
       }
       currentRoot.isWord = true;
    }

    public boolean isWord(String s) {
        TrieNode currentRoot = this;
        for(int i = 1; i <= s.length(); i++){
            String letters = s.substring(0, i);
            if(currentRoot.children.containsKey(letters)) {
                currentRoot = currentRoot.children.get(letters);
            }
            else{
                return false;
            }
        }
        return (currentRoot != null && currentRoot.isWord);
    }

    public String getAnyWordStartingWith(String s) {
        TrieNode currentRoot = this;
        String word = "";
        for(int i = 1; i <= s.length(); i++){
            String letters = s.substring(0, i);
            if(currentRoot.children.containsKey(letters)) {
                currentRoot = currentRoot.children.get(letters);
            }
        }
        while(!currentRoot.isWord){
            Set<String> keySet = currentRoot.children.keySet();
            Object[] keyArr = keySet.toArray();
            word = keyArr[0].toString();
            currentRoot = currentRoot.children.get(word);
        }
        return word;
    }

    public String getGoodWordStartingWith(String s) {
        TrieNode currentRoot = this;
        String word = "";
        for(int i = 1; i <= s.length(); i++){
            String letters = s.substring(0, i);
            if(currentRoot.children.containsKey(letters)) {
                currentRoot = currentRoot.children.get(letters);
            }
            else{
                return null;
            }
        }
        Set<String> keySet = currentRoot.children.keySet();
        Object[] keyArr = keySet.toArray();
        boolean found = false;
        for(int i = 0; i < keyArr.length; i ++){
          if(!currentRoot.children.get(keyArr[i]).isWord){
              currentRoot = currentRoot.children.get(keyArr[i]);
              found = true;
              break;
          }
        }
        if(!found){
            currentRoot= currentRoot.children.get(keyArr[0]);
        }
        while(!currentRoot.isWord){
            keySet = currentRoot.children.keySet();
            keyArr = keySet.toArray();
            word = keyArr[0].toString();
            currentRoot = currentRoot.children.get(word);
        }
        return word;
    }
}
