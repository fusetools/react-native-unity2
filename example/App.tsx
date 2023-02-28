import React from "react"
import {Button, View} from "react-native"
import {UnityModule, UnityView} from "react-native-unity2"

export default function App() {
  return (
    <UnityView
      style={{flex: 1, justifyContent: "flex-end"}}
      onMessage={onMessage}>
      <View
        style={{
          flexDirection: "row",
          alignContent: "space-between",
          justifyContent: "center",
        }}>
        <Button
          title={"setColor"}
          onPress={async () =>
            console.log(await cubeApi.setColor(randomColor()))
          }
        />
        <Button
          title={"toggleRotate"}
          onPress={async () => console.log(await cubeApi.toggleRotate())}
        />
        <Button
          title={"getAccount"}
          onPress={async () => console.log(await cubeApi.getAccount())}
        />
        <Button
          title={"fail"}
          onPress={async () => console.log(await cubeApi.fail())}
        />
      </View>
    </UnityView>
  )
}

const onMessage = (data: any) => {
  console.log("Unity message: " + data)
}

const cubeApi = {
  setColor(color: string) {
    return UnityModule.callMethod("Cube", "setColorRN", color)
  },

  toggleRotate() {
    return UnityModule.callMethod("Cube", "toggleRotateRN")
  },

  getAccount() {
    return UnityModule.callMethod("Cube", "getAccountRN")
  },

  fail() {
    return UnityModule.callMethod("Cube", "failRN")
  },
}

const randomColor = () => {
  return `#${Math.floor(Math.random() * 16777215)
    .toString(16)
    .padStart(6, "0")}`
}
