from keras.layers import Dropout
import tensorflow as tf
import pandas as pd
from sklearn.preprocessing import LabelEncoder
from keras.utils import np_utils
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
seed_num = 24

# load dataset
Hypertension = pd.read_csv('Hypertension_data.csv', sep=',')
target = Hypertension.pop('prevalentHyp') # num in raw dataset
hyp_feature_names = ['age', 'BMI', 'heartRate']
X = Hypertension[hyp_feature_names].values
Y = target.values

#Split 20 percent of X and y into testing. After that, Split 20 percent of X_train into validation
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state=seed_num) # random_state is set to a value for reproducible

# encode class values as integers
encoder = LabelEncoder()
encoder.fit(y_train)
encoded_Y = encoder.transform(y_train)
y_train = np_utils.to_categorical(encoded_Y)

def get_basic_model():
  model = tf.keras.Sequential([
    tf.keras.layers.Dense(3, input_dim=3, activation='relu'),
    tf.keras.layers.Dense(4, activation='relu'),
    tf.keras.layers.Dense(2, activation='sigmoid')
  ])

  model.compile(optimizer='adam',
                loss=tf.keras.losses.BinaryCrossentropy(),
                metrics=['accuracy'])
  return model

model = get_basic_model()
model.fit(X_train, y_train, epochs=1000, batch_size=2)

#make predictions and determine accuracy of the model
predictions = model.predict(X_test)
predictedClass = []
for i in range(0, 202):
    if predictions[i][0] > predictions[i][1]:
        predictedClass.append(0)
    else:
        predictedClass.append(1)

print('accuracy score:')
print(accuracy_score(y_test, predictedClass))


# Convert the model.
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Save the model.
with open('hypertension_model.tflite', 'wb') as f:
  f.write(tflite_model)