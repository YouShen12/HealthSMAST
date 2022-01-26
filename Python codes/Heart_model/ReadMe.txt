Source of raw dataset
https://www.kaggle.com/redwankarimsony/heart-disease-data/version/6

The data we need to train the model is extracted, manipulated and saved in heart_UCI.csv

Colum extracted:
age
sex
trestbps	
exang	
target (originally named num in raw dataset)

Manipulations:
num renamed to target
value for sex attribute (1 for male, 0 for female)
value for exang attribute (1 for true, 0 for false)
value for target (0 remain as 0, values greater than 1 -> 1) 
Rows with missing data were removed
The value 1,2,3 and 4 in original dataset indicates the stages of heart disease, 0 indicates no heart disease whereas 1 and above indicates heart disease
Therefore, since we only want to predict whether the user has heart disease or not, therefore we only need 0 and 1 (Values greater than 1 are converted to 1)
