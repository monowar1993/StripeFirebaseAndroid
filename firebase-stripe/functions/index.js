const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const express = require('express');
const cors = require('cors')({ origin: true });
const app = express();

// TODO: Remember to set token using >> firebase functions:config:set stripe.token="SECRET_STRIPE_TOKEN_HERE"
const stripe = require('stripe')(functions.config().stripe.token);

function stripeCharge(req, res) {
    const body = JSON.parse(req.body);
    const token = body.token.id;
    const amount = body.charge.amount;
    const currency = body.charge.currency;

    console.log(`tokenid ${token}`)
    console.log(`amount ${amount}`)
    console.log(`currency ${currency}`)

    // Charge card
    stripe.charges.create({
        amount,
        currency,
        description: 'Firebase Example',
        source: token,
    }).then(charge => {
        send(res, 200, {
            message: 'Success',
            charge,
        });
    }).catch(error => {
        console.error(error);
        send(res, 500, {
            error: err.message,
        });
    });
}

function send(res, code, body) {
    res.send({
        statusCode: code,
        headers: { 'Access-Control-Allow-Origin': '*' },
        body: JSON.stringify(body),
    });
}

app.use(cors);
app.post('/', (req, res) => {

    // Catch any unexpected errors to prevent crashing
    try {
        stripeCharge(req, res);
    } catch (error) {
        console.error(error);
        send(res, 500, {
            error: `The server received an unexpected error. Please try again and contact the site admin if the error persists.`,
        });
    }
});

exports.stripeCharge = functions.https.onRequest(app);

exports.stripeChargeCallable = functions.https.onCall((data, context) => {
    const token = data.token_id;
    const amount = data.amount;
    const currency = data.currency;
    const description = data.description;

    // console.log(`tokenId ${token}`)
    // console.log(`amount ${amount}`)
    // console.log(`currency ${currency}`)

    // Charge card
    return stripe.charges.create({
        'amount': amount,
        'currency': currency,
        'description': description,
        'source': token,
    }).then(charge => {
        console.log(charge)
        return {'charge': JSON.stringify(charge)};
    }).catch(error => {
        console.error(error);
        throw new functions.https.HttpsError('aborted', error.message, error.stringify);
    });
});

exports.retrieveStripeToken = functions.https.onCall((data, context) => {
    const tokenId = data.token_id;

    // console.log(`tokenId ${tokenId}`)

    return stripe.tokens.retrieve(tokenId).then(token => {
        console.log(token)
        return {'token': JSON.stringify(token)};
    }).catch(error => {
        console.error(error);
        throw new functions.https.HttpsError('aborted', error.message, error.stringify);
    });
});