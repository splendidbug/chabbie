package com.example.android.chabbie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;

import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;


public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private AIConfiguration config;
    private AIService aiService;

    EditText editText;
     Button send;
    TextView tv1, tv2, tv3;
    AIResponse response;
    AIRequest aiRequest;
    private AIDataService aiDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference myRef = database.getReference("message");
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        send = findViewById(R.id.send);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        myRef.setValue("hmmm");
        config = new AIConfiguration("34a9c141034946c2a5c043d6018125d7",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);

        aiDataService = new AIDataService(this, config);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                sendUserRequest(message);
            }
        });

    }

    public void onResult(final AIResponse response) {
        Result result = response.getResult();
        tv1.setText(getString(R.string.input , result.getResolvedQuery()));
        tv2.setText(getString(R.string.answer , result.getFulfillment().getSpeech()));
        tv3.setText(getString(R.string.intent , result.getMetadata().getIntentName()));
    }


    private void sendUserRequest(final String queryString) {


        final GetAIResponse getAIResponseTask = new GetAIResponse(aiDataService);
        getAIResponseTask.setListener(new GetAIResponse.Listener() {

            @Override
            public void onSuccess(AIResponse response, AIError error) {

                getAIResponseTask.setListener(null);

                if (response != null) {
                    onResult(response);
                    editText.setText("");
                } else {
                    Log.e("error:", "Error from AI service: " + error);
                }
            }
        });

        getAIResponseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, queryString);

    }

    static class GetAIResponse extends AsyncTask<String, Void, AIResponse> {

        private AIError aiError;
        private Listener listener;
        WeakReference<AIDataService> weakaiDataService;

        public GetAIResponse(AIDataService aiDataService) {
            this.weakaiDataService = new WeakReference(aiDataService);
        }


        interface Listener {
            void onSuccess(AIResponse response, AIError error);
        }

        @Override
        protected AIResponse doInBackground(final String... params) {
            final AIRequest request = new AIRequest();
            String query = params[0];
            if (!TextUtils.isEmpty(query))
                request.setQuery(query);

            RequestExtras requestExtras = null;
            try {
                return weakaiDataService.get().request(request, requestExtras);
            } catch (final AIServiceException e) {
                aiError = new AIError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final AIResponse response) {

            listener.onSuccess(response, aiError);
        }

        public void setListener(Listener listener) {
            this.listener = listener;
        }


    }

}
