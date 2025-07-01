package com.temptationjavaisland.wemeet.model;

/**
 * Classe astratta base per rappresentare il risultato di un'operazione.
 * Può essere un successo (con dati) o un errore (con messaggio).
 */
public abstract class Result {

    private Result() {}


    //Metodo che restituisce true se il risultato è un successo, false se è un errore.
    public boolean isSuccess() {
        return !(this instanceof Error);
    }

    //Sottoclasse per rappresentare un successo nella chiamata eventi.
    //Contiene un oggetto EventAPIResponse come risultato.
    public static final class EventSuccess extends Result {
        private final EventAPIResponse eventAPIResponse;

        public EventSuccess(EventAPIResponse eventAPIResponse) {
            this.eventAPIResponse = eventAPIResponse;
        }

        public EventAPIResponse getData() {
            return eventAPIResponse;
        }
    }

    //Sottoclasse per rappresentare un successo nella chiamata utente.
    //Contiene un oggetto User come risultato.
    public static final class UserSuccess extends Result {
        private final User user;

        public UserSuccess(User user) {
            this.user = user;
        }

        public User getData() {
            return user;
        }
    }


    //Sottoclasse per rappresentare un errore.
    //Contiene un messaggio di errore testuale.
    public static final class Error extends Result {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
