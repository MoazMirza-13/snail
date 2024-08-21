import {PermissionsAndroid} from 'react-native';

export const callPermissions = async () => {
  try {
    const permissions = await PermissionsAndroid.requestMultiple([
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
    ]);
    console.log('Permissions are:', permissions);
  } catch (err) {
    console.warn(err);
  }
};
