<h1 align="center">
  <br>
  <a href="https://github.com/mu54omd/u-llama/"><img src="https://github.com/mu54omd/u-llama/blob/master/app/src/main/ic_launcher-playstore.png" alt="ullama" width="200"></a>
  <br>
  ullama
  <br>
</h1>

<h3 align="center"> A simple but powerful user interface for <a href="https://ollama.com">ollama</a></h3>

<p align="center">
  <a href="https://github.com/mu54omd/u-llama/releases/tag/v1.0.35"><img src="https://img.shields.io/badge/release-v1.0.35-green" alt="Release Version"></a>
  <a href="https://kotlinlang.org/docs/whatsnew2020.html"><img src="https://img.shields.io/badge/Kotlin-v2.0.20-red" alt="Kotlin Version"></a>
  <a href=""><img src="https://img.shields.io/badge/OS-Android-orange" alt="OS"></a>
  <a href="https://developer.android.com/tools/releases/platforms#8.0"><img src="https://img.shields.io/badge/minSdk-26-blue" alt="Minimum Sdk"></a>
</p>


<p>This application offers a simple yet powerful user interface for Ollama. With it, you can easily connect to your local models and start a chat! You can also adjust model parameters for improved responses. Additionally, you can attach documents to the model using the robust ObjectBox library. </p>


<p align="center">
    <img src="https://github.com/mu54omd/u-llama/blob/master/app/src/main/res/raw/image1.png" alt="ullama picture">
</p>


## Key Features
* **Connect to local ollama server**
* **Support parameters tuning for LLMs**
* **Attach images and documents(pdf/docx/txt) to the model using RAG**
* **Use on-device vector database for fast searching in embedding vectors**
* **Offline TTS (text-to-speech) for reading LLMs message**
* **Adaptive layout for phone/tablet/fold devices**

## Libraries
* **[Apache-Poi](https://poi.apache.org)**: the Java API for Microsoft Documents.
* **[itextpdf](https://itextpdf.com)**: a library for creating and manipulating PDF files.
* **[Retrofit](https://square.github.io/retrofit/)**: a type-safe HTTP client for Android and Java.
* **[Hilt](https://dagger.dev/hilt/)**: a dependency injection library for Android.
* **[Room](https://developer.android.com/jetpack/androidx/releases/room#2.7.0)**: an abstraction layer over SQLite to allow for more robust database access.
* **[ObjectBox](https://objectbox.io)**: the on-device database and SQLite alternative for object and vector data.


## How To Use
You can download the source code and build it with android-studio or install the [release version](https://github.com/mu54omd/u-llama/releases/tag/v1.0.35). After that, put the ip of your local server in the setting screen and save this change. Now you are ready to start a chat with the models.

## Known Issues
* _It take some times to delete the imported files if they create too many chunks._ 

## To-Do
1. _Add online mode for using OpenAI_
2. _Migrate to KMP (Kotlin Multiplatform)_

## Medium Article About This Application
[A Chat Application for Local Large Language Models (LLMs)](https://medium.com/@owmo13/a-chat-application-for-local-large-language-models-llms-89411c6b1d6a)



