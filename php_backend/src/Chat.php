<?php
namespace MyApp;
use Ratchet\MessageComponentInterface;
use Ratchet\ConnectionInterface;

class Chat implements MessageComponentInterface {

    private $clients;

    /**
     * Chat constructor.
     * @param $client
     */
    public function __construct()
    {
        $this->clients = array();
    }


    public function onOpen(ConnectionInterface $conn) {
        $this->clients[] = $conn;
        echo "new connection";
    }

    public function onMessage(ConnectionInterface $from, $msg) {
        foreach ($this->clients as $client){
            if ($client != $from){
                $client->send($msg);
            }
        }
    }

    public function onClose(ConnectionInterface $conn) {
        echo "connection closed";
    }

    public function onError(ConnectionInterface $conn, \Exception $e) {
    }
}