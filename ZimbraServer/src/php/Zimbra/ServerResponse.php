<?php
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#

define('ServerResponse_EOL', "\r\n");
define('ServerResponse_BOUNDARY', "----------------314159265358979323846");
define('ServerResponse_CHUNK_SIZE', 8192);

/**
 * Assembles and writes multipart MIME response to send to the client.
 */
class ServerResponse {
    var $streams;
    var $params;

    /**
     * Adds a string parameter to the server response.
     */
    function addParameter($name, $value) {
        $this->params[$name] = $value;
    }

    /**
     * Adds a stream to the server response.
     */
    function addStream($name, $filename, $mode = "rb") {
        $this->streams[$name] = array("filename" => $filename, "mode" => $mode);
    }

    /**
     * Writes the multipart response that includes headers, parameters
     * and streams.
     */
    function writeContent() {
        // Set header to text/plain to make it easier to debug 
        // with a browser
        header("Content-Type: text/plain; charset=UTF-8");
        header("Content-Transfer-Encoding: 8bit");

        // Write multipart header
        echo "Content-Type: multipart/form-data;" . ServerResponse_EOL .
               "boundary=" . ServerResponse_BOUNDARY . ServerResponse_EOL .
               ServerResponse_EOL;

        // Write params
        foreach ($this->params as $name => $value) {
            echo "--" . ServerResponse_BOUNDARY . ServerResponse_EOL .
                "Content-Disposition: form-data; name=\"$name\"" . ServerResponse_EOL .
                ServerResponse_EOL .
                $value . ServerResponse_EOL;
        }

        // Write streams
        if ($this->streams) {
            foreach ($this->streams as $name => $value) {
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
                echo "--" . ServerResponse_BOUNDARY . ServerResponse_EOL .
                    "Content-Disposition: form-data; name=\"$name\"" . ServerResponse_EOL .
                    ServerResponse_EOL;

                // Write data
                while (!feof($inHandle)) {
                    $chunk = fread($inHandle, ServerResponse_CHUNK_SIZE);
                    fwrite($outHandle, $chunk);
                }
                fwrite($outHandle, ServerResponse_EOL);
            
                // Close filehandles
                fclose($inHandle);
                fclose($outHandle);
            }
        }

        // End response
        echo "--" . ServerResponse_BOUNDARY . "--" . ServerResponse_EOL;
    }
}

?>
