<template>
  <div style="padding: 10px; margin-bottom: 50px">
    <el-row>
      <el-col :span="4">
        <div style="position: fixed">
          <div style="position: relative;left: 0;top: 30px">
            用户昵称：
          </div>
          <div v-if="user.username===''" style="position: relative;left: 100px">
            <el-input v-model="inputUsername" clearable :autofocus="true" style="position: fixed; width: 130px;"/>
            <div style="color: red;position: fixed;margin-top: 50px;margin-left: -100px">
            当前状态:未连接
           </div>
          </div>
          <span v-else style="position: relative;left: 80px">
            <span style="position: fixed;margin-top: 10px;margin-left: 18px">
              {{ userNameForShow }}
            </span>
            <span style="color: #43e143;position: fixed;margin-top: 50px;margin-left: -80px">
            当前状态:已连接
           </span>
          </span>
          <span v-if="user.username===''" style="position: relative;left: 150px">
            <el-button type="primary" size="mini" @click="linking"
                       style="position:relative;left: 100px;margin-top: 4px">完成</el-button>
          </span>
          <span v-else style="position: relative;left: 150px">
            <el-button type="primary" size="mini" @click="updateUsername"
                       style="position:relative;left: 100px;margin-top: 4px">修改</el-button>
          </span>
        </div>
        <el-card style="width: 300px; height: 300px; color: #333;margin-top: 100px">
          <div style="padding-bottom: 10px; border-bottom: 1px solid #ccc">
            在线用户
            <span style="font-size: 12px">
            （点击聊天气泡开始聊天）
          </span>
          </div>
          <div style="padding: 10px 0">
            <span>世界聊天</span>
            <i class="el-icon-chat-dot-round" style="margin-left: 10px; font-size: 16px; cursor: pointer"
               @click="intoWorld()"></i>
            <span style="font-size: 12px;color: limegreen; margin-left: 5px"
                  v-if="isInWorld">world-chat click again to leave</span>
          </div>
          <div style="padding: 10px 0" v-for="user in users" :key="user.username">
            <span class="text-username">{{ user.username }}</span>
            <i class="el-icon-chat-dot-round" style="margin-left: 10px; font-size: 16px; cursor: pointer"
               @click="switchFriend(user.username)"></i>
            <i class="el-icon-bell" v-if="contains(requestUsers,user.username.trim())"
               style="margin-left: 10px; font-size: 16px;"></i>
            <span style="font-size: 12px;color: limegreen; margin-left: 5px"
                  v-if="user.username === chatUser&&isInWorld===false">chatting...</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="20">
        <div style="width: 800px; margin: 0 auto; background-color: white;
                    border-radius: 5px; box-shadow: 0 0 10px #ccc">
          <div style="text-align: center; line-height: 50px;">
            Web聊天室（{{ isInWorld ? '世界聊天' : chatUser }}）
          </div>
          <div id="main" style="height: 350px; overflow:auto; border-top: 1px solid #ccc" v-html="content"></div>
          <div style="height: 200px">
            <textarea v-model="text" style="margin: 0" @keydown.enter="send"></textarea>
            <div style="padding: 0;margin: 0">
              <button class="send-btn" @click="send" style="width: 100%">发送</button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8080';
let socket;
export default {
  name: "Im",
  data() {
    return {
      user: {
        username: ''
      },//当前用户
      isLinked: false,
      isInWorld: false,
      inputUsername: "",
      //在线用户列表
      users: [],
      //交谈的对象
      chatUser: '',
      //发送的信息文本
      text: "",
      //发送的信息内容
      messages: [],
      // 框内显示的内容
      content: '',
      //请求聊天的用户的用户名列表
      requestUsers:[]
    }
  },
  computed:{
    userNameForShow(){
      if(this.user.username.length>=8){
        return this.user.username.slice(0,8)+'..'
      }else {
        return this.user.username
      }
    }
  },
  methods: {
    intoWorld() {
      console.log('intoWorld')
      if (this.isLinked) {
        if (this.isInWorld === false) {
          if(this.chatUser!==''){
            this.alertChatUserWhenLeave(this.chatUser)
          }
          this.isInWorld = true;
          this.chatUser = '';
          axios("/chat/hello").then(res => {
            this.content = ''
            res.data.forEach(element => {
              console.log(element.username, element.text)
              if (element.username === this.user.username) {
                this.createContent(null, element.username, element.text)
              } else {
                this.createContent(element.username, null, element.text)
              }
            });
            this.updateScroll();
          })
          // this.updateScroll();//此函数必须和dom变化紧挨着
          this.Interval = setInterval(() => {
            axios("/chat/hello").then(res => {
              this.content = '';
              res.data.forEach(element => {
                console.log(element.username, element.text)
                if (element.username === this.user.username) {
                  this.createContent(null, element.username, element.text)
                } else {
                  this.createContent(element.username, null, element.text)
                }
              });
              this.updateScroll();
            })
          }, 2000);
        } else {
          this.isInWorld = false;
          this.content = '';
          clearInterval(this.Interval);
        }

      } else {
        window.alert("请先连接，连接后才能进入世界大厅哟")
      }

    },
    switchFriend(username) {
      if(username===this.chatUser){
        window.alert("已经和她连接了哟")
        return;
      }
      if(this.chatUser!==''){
        this.alertChatUserWhenLeave(this.chatUser)
      }
      if (this.isInWorld === false) {
        console.log('switchFriend')
        this.chatUser = username;
        this.content = '';
      } else {
        this.isInWorld = false;
        this.content = '';
        clearInterval(this.Interval);
        console.log('switchFriend')
        this.chatUser = username;
        this.content = '';
      }
      let message = {to: this.chatUser,function:'link'}
      socket.send(JSON.stringify(message));
    },
    send() {
      if (this.user.username === '') {
        this.$message({type: 'warning', message: "未连接哟，请连接"})
      } else {
        if (this.isInWorld === false) {//私聊发送按钮
          if (!this.chatUser) {
            this.$message({type: 'warning', message: "请选择聊天对象"})
            return;
          }
          if (!this.text) {
            this.$message({type: 'warning', message: "请输入内容"})
          } else {
            if (typeof (WebSocket) == "undefined") {
              console.log("您的浏览器不支持WebSocket");
            } else {
              console.log("您的浏览器支持WebSocket");
              // 组装待发送的消息 json
              // {"from": "zhang", "to": "admin", "text": "聊天文本"}
              let message = {from: this.user.username, to: this.chatUser, text: this.text}
              socket.send(JSON.stringify(message));  // 将组装好的json发送给服务端，由服务端进行转发
              this.messages.push({user: this.user.username, text: this.text})
              // 构建消息内容，本人消息
              this.createContent(null, this.user.username, this.text)
              this.text = '';
              this.updateScroll();
            }
          }
        } else {//世界大厅发送按钮
          // 构建消息内容，本人消息
          this.createContent(null, this.user.username, this.text)
          let username = this.user.username;
          let text = this.text;
          axios.post("/chat/add", {
            "username": username,
            "text": text,
          }).then(res => {
            console.log(res.data)
          }).catch(e => {
          })
          this.text = '';
          this.updateScroll();
        }
      }

    },
    createContent(remoteUser, nowUser, text) {  // 这个方法是用来将 json的聊天消息数据转换成 html的。
      let html
      // 当前用户消息
      if (nowUser) { // nowUser 表示是否显示当前用户发送的聊天消息，绿色气泡
        html = "<div class=\"el-row\" style=\"padding: 5px 0\">\n" +
          "  <div class=\"el-col el-col-22\" style=\"text-align: right;\">\n" +
          "  <div style=\"font-size:15px;color:black;background-color:white; \" class=\"left\">" + nowUser + "</div>  <div class=\"tip left\">" + text + "</div>\n" +
          "  </div>\n" +
          "  <div class=\"el-col el-col-2\">\n" +
          "  <span class=\"el-avatar el-avatar--circle\" style=\"height: 40px; width: 40px; line-height: 40px;\">\n" +
          "    <img src=\"https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png\" style=\"object-fit: cover;\">\n" +
          "  </span>\n" +
          "  </div>\n" +
          "</div>";
      } else if (remoteUser) {   // remoteUser表示远程用户聊天消息，蓝色的气泡
        html = "<div class=\"el-row\" style=\"padding: 5px 0\">\n" +
          "  <div class=\"el-col el-col-2\" style=\"text-align: right\">\n" +
          "  <span class=\"el-avatar el-avatar--circle\" style=\"height: 40px; width: 40px; line-height: 40px;\">\n" +
          "    <img src=\"https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png\" style=\"object-fit: cover;\">\n" +
          "  </span>\n" +
          "  </div>\n" +
          "  <div class=\"el-col el-col-22\" style=\"text-align: left;\">\n" +
          "    <div style=\"font-size:15px;background-color:white;\" class=\"right\">" + remoteUser + "</div><div class=\"tip right\">" + text + "</div>\n" +
          "  </div>\n" +
          "</div>";
      }
      console.log(html)
      this.content += html;
    },
    linking() {
      console.log("linking")
      if (this.user.username !== '') {
        window.alert("您已经连接了亲，您的用户名是: " + this.user.username)
      } else if (this.inputUsername !== '') {
        this.user.username = this.inputUsername;
        this.isLinked = true;
        let _this = this;
        if (typeof (WebSocket) == "undefined") {
          console.log("您的浏览器不支持WebSocket");
        } else {
          console.log("您的浏览器支持WebSocket");
          let socketUrl = "ws://localhost:8080/chat/myServer/ " + this.user.username;
          if (socket != null) {
            socket.close();
            socket = null;
          }
          // 开启一个websocket服务
          socket = new WebSocket(socketUrl);
          this.handleSocket(socket, _this);
        }
      } else {
        window.alert("请输入用户名呀亲，不输入是没法连接的哟")
      }
    },
    updateUsername() {
      this.user.username = ''
    },
    handleSocket(socket, _this) {
      //打开事件
      socket.onopen = function () {
        console.log("websocket已打开");
      };
      //  浏览器端收消息，获得从服务端发送过来的文本消息
      socket.onmessage = function (msg) {
        console.log("收到数据====" + msg.data)
        // 对收到的json数据进行解析， 类似这样的： {"users": [{"username": "zhang"},{ "username": "admin"}]}
        let data = JSON.parse(msg.data)
        console.log(data)
        if (data.users) {
          // 如果存在该users字段，说明这时公共消息，则如下处理
          _this.users = data.users.filter(user => user.username !== _this.user.username)
          // 获取当前连接的所有用户信息，并且排除自身，自己不会出现在自己的聊天列表里
        }else if(data.text==null){
          //如果不含text字段，说明这时一条通知类信息
          let tip = data.function;
          console.log("function:"+tip);
          let remoteFriend = data.from;
          if(tip==='leave'){
            _this.handleDisconnect(remoteFriend)
          }else if(tip==='link'){
            _this.handleRequest(remoteFriend)
          }
        } else {
          //如果包含text字段，则说明是常规聊天信息
          // 如果服务器端发送过来的json数据 不包含 users 这个key，那么发送过来的就是聊天文本json数据
          //   {"from": "zhang", "text": "hello"}
          if (data.from === _this.chatUser) {
            // 构建消息内容
            _this.createContent(data.from, null, data.text)
          }
        }
      };
      //关闭事件
      socket.onclose = function () {
        console.log("websocket已关闭");
      };
      //发生了错误事件
      socket.onerror = function () {
        console.log("websocket发生了错误");
      }
    },
    handleRequest(username){
      this.$message({
        message: "用户名为 "+username+" 的用户想和你通信",
        type: 'success'
      });
      this.requestUsers.push(username)
      console.log('iiiiiiiiiiiiiii'+this.requestUsers)
      console.log(this.requestUsers)
      console.log(this.requestUsers[0])
      console.log(this.contains(this.requestUsers,'111'))
      console.log(this.requestUsers[0].trim()===111)
    },
    handleDisconnect(username){
      this.$message({
        message: "用户名为 "+username+" 的用户断开了通信",
        type: 'warning'
      });
      for (let i = 0; i < this.requestUsers.length; i++) {
        if (this.requestUsers[i]===username){
          this.requestUsers.splice(i,1)
          console.log('iiiiiiiiiiiiiii'+this.requestUsers)
        }
      }
    },
    updateScroll() {
      this.$nextTick(() => {
        let container = this.$el.querySelector("#main");
        container.scrollTop = container.scrollHeight;
      })
    },
    alertChatUserWhenLeave(chatUser){
      let message = {from: this.user.username, to: chatUser, function: 'leave'}
      // 将组装好的json发送给服务端，由服务端进行转发
      socket.send(JSON.stringify(message));
    },
    contains(arr,e){
      for (let i = 0; i < arr.length; i++) {
        if(arr[i].trim()===e) return true
      }
      return false
    }
  }
}
</script>
<style>
.tip {
  color: white;
  text-align: center;
  border-radius: 10px;
  font-family: sans-serif;
  padding: 10px;
  width: auto;
  display: inline-block !important;
}

.right {
  background-color: deepskyblue;
}

.left {
  background-color: #66c213;
}

textarea {
  height: 160px;
  width: 100%;
  padding: 20px;
  margin: 0;
  font-size: 16px;
  font-family: "Times New Roman", Times, serif;
  border: none;
  border-top: 1px solid #ccc;
  outline: none;
  resize: none;
}

.send-btn {
  background: #d6e5e4;
  color: #000;
  outline: none;
  display: block;
  border: none;
  cursor: pointer;
  padding: 9px 38px;
  font-size: 14px;
  font-weight: bold;
  text-transform: uppercase;
  transition: 0.3s all;
  -webkit-transition: 0.3s all;
  -moz-transition: 0.3s all;
  -o-transition: 0.3s all;
  -ms-transition: 0.3s all;
}

.send-btn:hover {
  background: #62eade;
  color: #ffffff;
  transition: 0.3s all;
  -webkit-transition: 0.3s all;
  -moz-transition: 0.3s all;
  -o-transition: 0.3s all;
  -ms-transition: 0.3s all;
}
</style>
