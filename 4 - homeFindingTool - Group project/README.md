# Praelocate

### _A home relocation tool_

A web-app that would find the best location to find a house given certain significant locations as input.

## Authors:

- Jacques
- Markus
- Sam
- Brandon
- Willem

## Stack:

- Backend: simple Flask API
- Frontend: React app

## Requirements:

- Python 3.x
- NodeJS
- Google maps API key

## Environment setup:

### Backend

1. Setup virtual environment:

   `$ cd backend`

   `$ python -m venv .venv`

2. Activate virtual environment for:

   Windows:

   `$ .\.venv\Scripts\activate`

   Linux/Mac:

   `$ source .venv/bin/activate`

3. Now there should be a green `(.venv)` tag at the front of the terminal line
4. Install requirements (this includes flask):

   `$ pip install -r requirements.txt`

5. Files with the api key need to be created in both the frontend and backend:
5.1. For the frontend: 
	Inside the frontend directory create a file with the name .env with `REACT_APP_API_KEY=<api-key>' inside. Replace <api-key> with the api key you got for the Google maps api.
5.2. For the backend:
	Inside the backend directory create a file with the name api-key.txt with <api-key>' inside. Replace <api-key> with the api key you got for the Google maps api.

### Frontend

1. Install packages:

   `$ cd frontend`

   `$ npm install`

   `$ npm install --save @react-google-maps/api`

## Execution

### Backend

1. Open a new terminal instance in backend directory
2. Activate the venv as explained above
3. Start the flask app:

   `$ flask run`

### Test cases

Run:
`$ python -m unittest test_app.py`

### Frontend

1. Open a new terminal instance in frontend directory
2. Start the react app:

   `$ npm start`

