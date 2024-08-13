import React from 'react';
import {StyleSheet, Text, View, TouchableOpacity} from 'react-native';
import {useCallState} from './services/callState';

const App = () => {
  const {playSound} = useCallState();

  return (
    <View style={styles.body}>
      <TouchableOpacity onPress={playSound} style={styles.button}>
        <Text style={styles.buttonText}>
          Touch here to play the GACHA sound
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default App;

const styles = StyleSheet.create({
  body: {
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
    flex: 1,
  },
  button: {
    padding: 10,
    backgroundColor: 'black',
    borderRadius: 5,
    marginBottom: 15,
    color: 'white',
  },
  buttonText: {
    color: 'white',
  },
});
