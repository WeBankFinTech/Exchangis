const SOURCETYPE = {
    HIVE: 'hive',
    LOCAL_FS: 'local_fs',
    HDFS: 'hdfs',
    SFTP: 'sftp',
    ELASTICSEARCH: 'elasticsearch',
    MYSQL: 'mysql',
    ORACLE: 'oracle'
}
const TRANSFERTYPE = {
    STREAM: 'stream',
    RECORD: 'record'
}
const LOGBUTTON = {
    PRE: 'pre',
    NEXT: 'next'
}
const UNICODELIST = [
    '\\U0020',
    '\\U0022',
    '\\U0023',
    '\\U0024',
    '\\U0025',
    '\\U0026',
    '\\U0027',
    '\\U0028',
    '\\U0029',
    '\\U002A',
    '\\U002B',
    '\\U002C',
    '\\U002D',
    '\\U002E',
    '\\U002F',
    '\\U0030',
    '\\U0031',
    '\\U0032',
    '\\U0033',
    '\\U0034',
    '\\U0035',
    '\\U0036',
    '\\U0037',
    '\\U0038',
    '\\U0039',
    '\\U003A',
    '\\U003B',
    '\\U003C',
    '\\U003D',
    '\\U003E',
    '\\U003F',
    '\\U0040',
    '\\U0041',
    '\\U0042',
    '\\U0043',
    '\\U0044',
    '\\U0045',
    '\\U0046',
    '\\U0047',
    '\\U0048',
    '\\U0049',
    '\\U004A',
    '\\U004B',
    '\\U004C',
    '\\U004D',
    '\\U004E',
    '\\U004F',
    '\\U0050',
    '\\U0051',
    '\\U0052',
    '\\U0053',
    '\\U0054',
    '\\U0055',
    '\\U0056',
    '\\U0057',
    '\\U0058',
    '\\U0059',
    '\\U005A',
    '\\U005B',
    '\\U005C',
    '\\U005D',
    '\\U005E',
    '\\U005F',
    '\\U0060',
    '\\U0061',
    '\\U0062',
    '\\U0063',
    '\\U0064',
    '\\U0065',
    '\\U0066',
    '\\U0067',
    '\\U0068',
    '\\U0069',
    '\\U006A',
    '\\U006B',
    '\\U006C',
    '\\U006D',
    '\\U006E',
    '\\U006F',
    '\\U0070',
    '\\U0071',
    '\\U0072',
    '\\U0073',
    '\\U0074',
    '\\U0075',
    '\\U0076',
    '\\U0077',
    '\\U0078',
    '\\U0079',
    '\\U007A',
    '\\U007B',
    '\\U007C',
    '\\U007D',
    '\\U007E',
    '\\U00A1',
    '\\U00A2',
    '\\U00A3',
    '\\U00A4',
    '\\U00A5',
    '\\U00A6',
    '\\U00A7',
    '\\U00A8',
    '\\U00A9',
    '\\U00AA',
    '\\U00AB',
    '\\U00AC',
    '\\U00AE',
    '\\U00AF',
    '\\U00B0',
    '\\U00B1',
    '\\U00B2',
    '\\U00B3',
    '\\U00B4',
    '\\U00B5',
    '\\U00B6',
    '\\U00B7',
    '\\U00B8',
    '\\U00B9',
    '\\U00BA',
    '\\U00BB',
    '\\U00BC',
    '\\U00BD',
    '\\U00BE',
    '\\U00BF',
    '\\U00C1',
    '\\U00C2',
    '\\U00C3',
    '\\U00C4',
    '\\U00C5',
    '\\U00C6',
    '\\U00C7',
    '\\U00C8',
    '\\U00C9',
    '\\U00CA',
    '\\U00CB',
    '\\U00CC',
    '\\U00CD',
    '\\U00CE',
    '\\U00CF',
    '\\U00D0',
    '\\U00D1',
    '\\U00D2',
    '\\U00D3',
    '\\U00D4',
    '\\U00D5',
    '\\U00D6',
    '\\U00D7',
    '\\U00D8',
    '\\U00D9',
    '\\U00DA',
    '\\U00DB',
    '\\U00DC',
    '\\U00DD',
    '\\U00DE',
    '\\U00DF',
    '\\U00E0',
    '\\U00E1',
    '\\U00E2',
    '\\U00E3',
    '\\U00E4',
    '\\U00E5',
    '\\U00E6',
    '\\U00E7',
    '\\U00E8',
    '\\U00E9',
    '\\U00EA',
    '\\U00EB',
    '\\U00EC',
    '\\U00ED',
    '\\U00EE',
    '\\U00EF',
    '\\U00F0',
    '\\U00F1',
    '\\U00F2',
    '\\U00F3',
    '\\U00F4',
    '\\U00F5',
    '\\U00F6',
    '\\U00F7',
    '\\U00F8',
    '\\U00F9',
    '\\U00FA',
    '\\U00FB',
    '\\U00FC',
    '\\U00FD',
    '\\U00FE',
    '\\U00FF',
    '\\U0100',
    '\\U0101',
    '\\U0102',
    '\\U0103',
    '\\U0104',
    '\\U0105',
    '\\U2C60',
    '\\U2C61',
    '\\U2C62',
    '\\U2C63',
    '\\U2C64',
    '\\U2C65',
    '\\U2C66',
    '\\U2C67',
    '\\U2C68',
    '\\U2C69',
    '\\U2C6A',
    '\\U2C6B',
    '\\U2C6C',
    '\\U2C74',
    '\\U2C75',
    '\\U2C76',
    '\\U2C77',
    '\\UA720',
    '\\UA721',
    '\\UFFFC',
    '\\UFFFD'
]
export {
    SOURCETYPE,
    TRANSFERTYPE,
    LOGBUTTON,
    UNICODELIST
}