import tensorflow as tf
import os.path as path

pwd = os.path.realpath(__file__)
model_path = pwd+"/models"
converter = tf.lite.TFLiteConverter.from_saved_model(model_path)
tflite_model = converter.convert()

with open('model.tflite', 'wb') as f:
  f.write(tflite_model)
