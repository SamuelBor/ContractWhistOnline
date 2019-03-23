# A Web Platform for the Comparison of Strategies in a Trick-Based Card Game

## Motivation

Originally introduced to me by my father, Contract Whist is a trick-taking card game with a bidding aspect similar to that of the more popular game: Bridge.  The game is comprised of 14 rounds, with each round made up of three distinct stages. Players are tasked with predicting how many tricks they will take in a round, and are scored on the accuracy of this prediction. 

After a decade of informal family play, the opportunity to create an application that could simulate the game presented itself with advantages being twofold.

Firstly, while it had been theorised that a superior strategy existed, until now there had been no way to test one against another. This project aims to test this theory by simulated computer controlled agents playing the game many times. If such a superior strategy is found to exist, we will evaluate what factors influence it to be better than other strategies and judge to what extent it is so.

We discovered that there exist very few other investigations into this specific Whist variant.  Previous projects are focused either exclusively on strategy research or on simulating gameplay, but none were found that explore both.  Therefore, to be able to create a fully functioning game environment that agents can interact with - in the same capacity as humans can - while also investigating gameplay strategies, was thought to be an exciting prospect.

## Project Description

We aim to create an application capable of simulating computer-controlled agents playing Contract Whist. This application is comprised of a back-end running the game code, and a front-end interface. These two core components are connected by a web server so that the application can easily be hosted online. Through the use of this application, it is possible to compare strategies, and gain a deeper understanding of what aspects contribute to a successful one.

We implement 5 separate agents, each of whom play the game with a different gameplay strategy. The strategy they incorporate is exclusively used in the gameplay stage, as this is where points are primarily won or lost. Their strategy is also isolated to the gameplay stage because the bidding stage uses a fixed prediction method amongst the agents as a control variable. If the agents also varied in how they bid at the start of the game, the perception of a strategy's effectiveness could be distorted and cause a successful strategy to go unnoticed due to an inaccurate prediction. The strategies implemented into the agents include both a winning strategy and a losing strategy, which they can switch between based on whether they are meeting their contract or not. 

Through the examination of the implemented strategies, we are able to not only make a conclusion as to what factors contribute to a successful strategy but also gain insight into whether an optimal strategy could exist. While investigations into optimal strategies for imperfect information games have been conducted in the past, this is one of the first to examine this specific Whist variant.

