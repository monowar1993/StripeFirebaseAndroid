# StripeFirebaseAndroid
Sample application that implements Stripe with Firebase Functions

## Functions Code

See file [functions/index.js](firebase-stripe/functions/index.js) for the code.

The dependencies are listed in [functions/package.json](firebase-stripe/functions/package.json).

## Deploy and test

To test this integration:
 - Create a Firebase Project using the [Firebase Developer Console](https://console.firebase.google.com)
 - Enable billing on your project by switching to the Blaze or Flame plan. See [pricing](https://firebase.google.com/pricing/) for more details. This is required to be able to do requests to non-Google services.
 - Install [Firebase CLI Tools](https://github.com/firebase/firebase-tools) if you have not already and log in with `firebase login`.
 - Configure this sample to use your project using `firebase use --add` and select your project.
 - Install dependencies locally by running: `cd functions; npm install; cd -`
 - [Add your Stripe API Secret Key](https://dashboard.stripe.com/account/apikeys) to firebase config:
     ```bash
     firebase functions:config:set stripe.token=<YOUR STRIPE SECRET KEY>
     ```
 - Deploy your function using `firebase deploy --only functions`
 - Deploy your hosting using `firebase deploy --only hosting`
 - Test your Stripe integration by viewing your deployed site `firebase open hosting:site`
