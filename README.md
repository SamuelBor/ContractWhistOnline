# A Web Platform for the Comparison of Strategies in a Trick-Based Card Game

## Motivation

Contract Whist was first introduced to me by my father when I was around 10 years old,  and it was inturn introduced to him around 20 years earlier by a close friend of his.  It is a trick-based card game witha bidding aspect similar to that of the more widespread game:  Bridge.

After many years of being evenly matched at the game with other members of my family, I took a great interest in researching different game strategies that could yield advantage over one anotherafter being simulated in-game a large number of times. While we had often theorised that a winning strategy existed, until now we had no way to test one against another. This project aims to resolve this dispute; a dispute that spans the best part of a decade.

Through many hours of research, I discovered that there exist very few other investigations into this specific whist variant.  Previous projects that I did find focused either exclusively on strategy research or on simulating gameplay, but none were found that explore both. Therefore, to be able to create a fully functioning game environment that agents can interact with - in the same capacity as humans can - while also investigating gameplay strategies, is a very exciting prospect.

## Project Description

We aim to create a fully functional application that can simulate computer-controlled agents playing contract whist. This application will be comprised of a back-end running the game code, and a front-end interface added to increase usability. These two core components will be connected by a web server such that the application can easily be hosted online after the project's conclusion. Through the use of this application, it will be possible to compare strategies from the set that we create, and gain a deeper understanding of what aspects contribute to a successful strategy.

We will be implementing at least 5 separate agents, each of whom will play the game with a different gameplay strategy. The strategy they incorporate will be exclusively used in the gameplay stage, as this is where points are primarily won or lost. The strategy will also be isolated to this stage of the game as the bidding stage, while technical in itself, will be using a consistent prediction method among the agents as a control variable. If the agents also differed in how they bid at the start of the game, this could distort the perception of a strategy's effective and cause a successful strategy to go unnoticed due to an unsuccessful prediction method. The strategies implemented into the agents will include both a winning strategy and a losing strategy, which they will be able to switch between based on whether they have met their contract or not. 

Through the examination of the implemented strategies, we will be able to not only make a conclusion as to what factors contribute the most to a successful strategy but also gain some insight into whether an optimal strategy could exist. While investigations into optimal strategies for imperfect information games have been conducted in the past, this is one of the first to examine this specific whist variant.
