const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  stompClient.subscribe('/topic/bookAdded', msg => {
    const book = JSON.parse(msg.body);
    alert('Dodano książkę: ' + book.title);
  });
  stompClient.subscribe('/topic/bookUpdated', msg => {
    const book = JSON.parse(msg.body);
    alert('Zaktualizowano książkę: ' + book.title);
  });
  stompClient.subscribe('/topic/bookDeleted', msg => {
    alert('Usunięto książkę o ID: ' + msg.body);
  });
});
