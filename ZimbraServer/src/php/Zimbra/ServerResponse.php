<?php

/**
 * Assembles and writes multipart MIME response to send to the client.
 */
class ServerResponse {
    const EOL = "\r\n";
    const BOUNDARY = "----------------314159265358979323846";
    const CHUNK_SIZE = 8192;

    /**
     * Adds a string parameter to the server response.
     */
    public function addParameter($name, $value) {
        $this->mParams[$name] = $value;
    }

    /**
     * Adds a stream to the server response.
     */
    public function addStream($name, $filename, $mode = "rb") {
        $this->mStreams[$name] = array("filename" => $filename, "mode" => $mode);
    }

    /**
     * Writes the multipart response that includes headers, parameters
     * and streams.
     */
    public function writeContent() {
        // Set header to text/plain to make it easier to debug 
        // with a browser
        header("Content-Type:text/plain");

        // Write multipart header
        echo "Content-Type: multipart/form-data;" . ServerResponse::EOL .
               "boundary=" . ServerResponse::BOUNDARY . ServerResponse::EOL .
               ServerResponse::EOL;

        // Write params
        foreach ($this->mParams as $name => $value) {
            echo "--" . ServerResponse::BOUNDARY . ServerResponse::EOL .
                "Content-Disposition: form-data; name=\"$name\"" . ServerResponse::EOL .
                ServerResponse::EOL .
                $value . ServerResponse::EOL;
        }

        // Write streams
        foreach ($this->mStreams as $name => $value) {
            // Initialize filehandles
            $filename = $value["filename"];
            $mode = $value["mode"];
            $inHandle = fopen($filename, $mode);
            if ($inHandle == FALSE) {
                error_log("Zimbra::ServerResponse: unable to open '$filename', '$mode'");
                continue;
            }
            $outHandle = fopen("php://output", "wb");
            if ($outHandle == FALSE) {
                error_log("Zimbra::ServerResponse: unable to open php://output");
                break;
            }

            // Write part header
            echo "--" . ServerResponse::BOUNDARY . ServerResponse::EOL .
                "Content-Disposition: form-data; name=\"$name\"" . ServerResponse::EOL .
                ServerResponse::EOL;

            // Write data
            while (!feof($inHandle)) {
                $chunk = fread($inHandle, ServerResponse::CHUNK_SIZE);
                fwrite($outHandle, $chunk);
            }
            fwrite($outHandle, ServerResponse::EOL);
            
            // Close filehandles
            fclose($inHandle);
            fclose($outHandle);
        }

        // End response
        echo "--" . ServerResponse::BOUNDARY . "--" . ServerResponse::EOL;
    }
}

?>
