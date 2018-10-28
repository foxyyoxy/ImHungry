import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;

public class ImHungry implements SpeechletV2 {

    private static final String ITEM_SLOT = "Item";

    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        //initialization logic
    }

    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {


        String speechOutput =
                "Welcome to the Minecraft Helper. You can ask a question like, "
                        + "what's the recipe for a chest? ... Now, what can I help you with?";
        // If the user either does not reply to the welcome message or says
        // something that is not understood, they will be prompted again with this text.
        String repromptText = "For instructions on what you can say, please say help me.";

        // Here we are prompting the user for input
        return newAskResponse(speechOutput, repromptText);
    }

    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("RecipeIntent".equals(intentName)) {
            return getRecipe(intent);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelp();
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } else {
            String errorSpeech = "This is unsupported.  Please try something else.";
            return newAskResponse(errorSpeech, errorSpeech);
        }
    }

    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        // any cleanup logic goes here
    }

    private SpeechletResponse getRecipe(Intent intent) {
        Slot itemSlot = intent.getSlot(ITEM_SLOT);
        if (itemSlot != null && itemSlot.getValue() != null) {
            String itemName = itemSlot.getValue();

            // Get the recipe for the item
            String recipe = Recipes.get(itemName);

            if (recipe != null) {
                // If we have the recipe, return it to the user.
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText(recipe);

                SimpleCard card = new SimpleCard();
                card.setTitle("Recipe for " + itemName);
                card.setContent(recipe);

                return SpeechletResponse.newTellResponse(outputSpeech, card);
            } else {
                // We don't have a recipe, so keep the session open and ask the user for another
                // item.
                String speechOutput =
                        "I'm sorry, I currently do not know the recipe for " + itemName
                                + ". What else can I help with?";
                String repromptSpeech = "What else can I help with?";
                return newAskResponse(speechOutput, repromptSpeech);
            }
        } else {
            // There was no item in the intent so return the help prompt.
            return getHelp();
        }
    }

    /**
     * Creates a {@code SpeechletResponse} for the HelpIntent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelp() {
        String speechOutput =
                "You can ask questions about minecraft such as, what's "
                        + "the recipe for a chest, or, you can say exit... "
                        + "Now, what can I help you with?";
        String repromptText =
                "You can say things like, what's the recipe for a"
                        + " chest, or you can say exit... Now, what can I help you with?";
        return newAskResponse(speechOutput, repromptText);
    }

    /**
     * Wrapper for creating the Ask response. The OutputSpeech and {@link Reprompt} objects are
     * created from the input strings.
     *
     * @param stringOutput
     *            the output to be spoken
     * @param repromptText
     *            the reprompt for if the user doesn't reply or is misunderstood.
     * @return SpeechletResponse the speechlet response
     */
    private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);

        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
    }
}
