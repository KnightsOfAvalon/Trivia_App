package com.example.trivia.data;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    // initialize variables
    ArrayList<Question> questionArrayList = new ArrayList<>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";


    public List<Question> getQuestions(final AnswerListAsyncResponse callBack) {
        // API request for JSON array
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Makes a new question object for each item in the JSON array
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                Question question = new Question();
                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                                // Add question objects to list
                                questionArrayList.add(question);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (callBack != null) {
                            callBack.processFinished(questionArrayList);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        // Adds the jsonArrayRequest to the queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        // Returns the ArrayList that results from the API request
        return questionArrayList;
    }

}
