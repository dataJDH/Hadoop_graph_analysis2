# Hadoop_graph_analysis2
MapReduce program to compute the distribution of a graph’s node degree differences

Uses Microsoft Azure with a MapReduce program to compute the distribution of a graph’s node degree differences (see example below). 

it uses the data files small.tsv.

The file stores a list of edges as tab-separated-values. Each line represents a single edge consisting of two columns: (Source, Target), each of which is separated by a tab. Node IDs are positive integers and the rows are already sorted by Source.

Source        Target

0                0

0                1

1                1

1                2

2                3

The code should accepts two arguments upon running. The first argument (args[0]) will be a path for the input graph file, and the second argument (args[1]) will be a path for output directory. The default output mechanism of Hadoop will create multiple files on the output directory such as part-00000, part-00001, which will have to be merged and downloaded to a local directory (instructions on how to do this are provided below).

The format of the output should be as follows. Each line of your output is of the format

diff        count

where

(1) diff is the difference between a node’s out-degree and in-degree (i.e., out-degree minus in-degree); and

(2) count is the number of nodes that have the value of difference (specified in 1).

The out-degree of a node is the number of edges where that node is the Source. The in-degree of a node is the number of edges where that node is the Target. diff and count must be separated by a tab (\t), and the lines do not have to be sorted. When the source and target is the same node, such as [0, 0] in the example, node “0” should be counted in both in-degree and out-degree.

The following result is computed based on the graph above.

-1        1

0        2

1        1

The explanation of the above example result:

Output
	

Explanation

-1        1
	

There are 1 nodes (node 3) whose degree difference is -1

0        2
	

There are 2 nodes (node 1 and node 2) whose degree is 0.

1        1
	

There is 1 node (node 0) whose degree difference is 1.

The `src` directory contains a main Java file
`pom.xml` contains necessary dependencies and compile configurations for the question. 

To compile, you can run the command in the directory which contains `pom.xml`.

`mvn clean package`

This command will generate a single JAR file in the target directory (i.e. target/q4-1.0.jar).

Creating Clusters in HDInsight using the Azure portal:

Azure HDInsight is an Apache Hadoop distribution. This means that it handles large amounts of data on demand. The next step is to use Azure’s web-based management tool to create a Linux cluster. Follow the recommended steps shown here to create a new cluster.

At the end of this process, you will have created and provisioned a New HDInsight Cluster and Storage (the provisioning will take some time depending on how many nodes you chose to create). Please record the following important information (we also recommend that you take screenshots) so you can refer to them later:

    Cluster login credentials
    SSH credentials
    Resource group
    Storage account
    Container credentials


Uploading data files to HDFS-compatible Azure Blob storage:

Main steps from for uploading data files to your Azure Blob storage here:

- Open a command prompt, bash, or other shell, and use az login command to authenticate to your Azure subscription. When prompted, enter the username and password for your subscription.

- az storage account list command will list the storage accounts for your subscription.

- az storage account keys list --account-name <storage-account-name> --resource-group <resource-group-name> command should return “key1” and “key2”. Copy the value of “key1” because it will be used in the next steps.

- az storage container list --account-name <storage-account-name> --account-key <key1-value> command will list your blob containers.

- az storage blob upload --account-name <storage-account-name> --account-key <key1-value> --file <small or large .tsv> --container-name <container-name> --name <new-blob-name>/<small or large .tsv> command will upload the source file to your blob storage container. <new-blob-name> is the folder name you will create and where you would like to upload tsv files to. If the file is uploaded successfully, you should see “Finished [#####] 100.0000%”.

Using these steps, upload small.tsv to your blob storage container. After that, you can find the uploaded files in storage blobs at Azure (portal.azure.com) by clicking on “Storage accounts” in the left side menu and navigating through your storage account (<Your Storage Account> -> <”Blobs” in the overview tab> -> <Select your Blob container to which you’ve uploaded the dataset> -> <Select the relevant blob folder>). For example, “jonDoeStorage” -> “Blobs” -> “jondoecluster-xxx” -> “jdoeSmallBlob” -> “small.tsv”. After that write your hadoop code locally and convert it to a jar file using the steps mentioned above.

Uploading your Jar file to HDFS-compatible Azure Blob storage:

Azure Blob storage is a general-purpose storage solution that integrates with HDInsight. Your Hadoop code should directly access files on the Azure Blob storage.

Upload the jar file created in the first step to Azure storage using the following command:

scp <your-relative-path>/q4-1.0.jar <ssh-username>@<cluster-name>-ssh.azurehdinsight.net:

You will be asked to agree to connect by typing “yes” and your cluster login password. Then you will see q4-1.0.jar is uploaded 100%.

SSH into the HDInsight cluster using the following command:

ssh <ssh-username>@<cluster-name>-ssh.azurehdinsight.net

<ssh-username> is what you have created in step 10 in the flow.

Note: if you see the warning - REMOTE HOST IDENTIFICATION HAS CHANGED, you may clean /home/<user>/.ssh/known_hosts” by using the command rm ~/.ssh/known_hosts. Please refer to host identification.

 

Run the ls command to make sure that the q4-1.0.jar file is present.

To run your code on the small.tsv file, run the following command:

yarn jar q4-1.0.jar edu.gatech.cse6242.Q4 wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/<new-blob-name>/small.tsv wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/smalloutput

Command format: yarn jar jarFile packageName.ClassName dataFileLocation outputDirLocation

The output will be located in the directory: wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/smalloutput

If there are multiple output files, merge the files in this directory using the following command:

hdfs dfs -cat wasbs://<container-name>@<storage-account-name>.blob.core.windows.net/smalloutput/* > small.out

Command format: hdfs dfs -cat location/* >outputFile

Then you may exit to your local machine using the command:

exit

You can download the merged file to the local machine (this can be done either from Azure Portal or by using the scp command from the local machine). Here is the scp command for downloading this output file to your local machine:

scp <ssh-username>@<cluster-name>-ssh.azurehdinsight.net:/home/<ssh-username>/small.out <local directory>

Using the above command from your local machine will download the small.out file into the local directory.
