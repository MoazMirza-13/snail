import SoundPlayer from 'react-native-sound-player';
const CallDetectorManager: any = require('react-native-call-detection');
import {useState} from 'react';

export const useCallState = () => {
  const [callDetector, setCallDetector] = useState(null);

  const playSound = async () => {
    try {
      SoundPlayer.playAsset(
        require('../android/app/src/main/res/raw/gacha.mp3'),
      );
      console.log('gacha played');
    } catch (error) {
      console.log('cannot play the song file', error);
    }
  };

  const startListenerTapped = () => {
    const detector = new CallDetectorManager(
      (event: string) => {
        console.log(event);
        if (event === 'Disconnected') {
          playSound();
        } else if (event === 'Incoming') {
        } else if (event === 'Offhook') {
          console.log('Offhook call');
          playSound();
        } else if (event === 'Missed') {
          playSound();
        }
      },
      false, // no need to read phone numbers
      () => {
        console.log('permission denied');
      },
      {
        title: 'Phone State Permission',
        message:
          'This app needs access to your phone state in order detect calls and play the GACHA sound.',
      },
    );
    setCallDetector(detector); // might use in future !
  };

  return {
    playSound,
    startListenerTapped,
  };
};
